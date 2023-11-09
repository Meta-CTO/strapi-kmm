package com.swensonhe.strapikmm.backgrounddownloader

import                                                                                                                                                                                                                                           android.annotation.SuppressLint
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.swensonhe.strapikmm.util.applyIf
import com.tonyodev.fetch2.ACTION_TYPE_CANCEL
import com.tonyodev.fetch2.ACTION_TYPE_CANCEL_ALL
import com.tonyodev.fetch2.ACTION_TYPE_DELETE
import com.tonyodev.fetch2.ACTION_TYPE_DELETE_ALL
import com.tonyodev.fetch2.ACTION_TYPE_INVALID
import com.tonyodev.fetch2.ACTION_TYPE_PAUSE
import com.tonyodev.fetch2.ACTION_TYPE_PAUSE_ALL
import com.tonyodev.fetch2.ACTION_TYPE_RESUME
import com.tonyodev.fetch2.ACTION_TYPE_RESUME_ALL
import com.tonyodev.fetch2.ACTION_TYPE_RETRY
import com.tonyodev.fetch2.ACTION_TYPE_RETRY_ALL
import com.tonyodev.fetch2.Download
import com.tonyodev.fetch2.DownloadNotification

import com.tonyodev.fetch2.DownloadNotification.ActionType.*
import com.tonyodev.fetch2.EXTRA_ACTION_TYPE
import com.tonyodev.fetch2.EXTRA_DOWNLOAD_ID
import com.tonyodev.fetch2.EXTRA_DOWNLOAD_NOTIFICATIONS
import com.tonyodev.fetch2.EXTRA_GROUP_ACTION
import com.tonyodev.fetch2.EXTRA_NAMESPACE
import com.tonyodev.fetch2.EXTRA_NOTIFICATION_GROUP_ID
import com.tonyodev.fetch2.EXTRA_NOTIFICATION_ID
import com.tonyodev.fetch2.Fetch
import com.tonyodev.fetch2.FetchNotificationManager
import com.tonyodev.fetch2.R
import com.tonyodev.fetch2.Status
import com.tonyodev.fetch2.util.onDownloadNotificationActionTriggered

/**
 * The default notification manager used by Fetch. This class can be extended to create custom
 * notification managers.
 *
 * @param context The application context
 * @param canPauseDownloads If true, downloads can be paused from the notification.
 * @param canCancelDownloads If true, downloads can be cancelled from the notification.
 */
internal abstract class DefaultFetchNotificationManager(
    private val context: Context,
    private val canPauseDownloads: Boolean,
    private val canCancelDownloads: Boolean
) : FetchNotificationManager {

    // The notification manager to use for the on going notification
    private val notificationManager by lazy {
        // Init notification manager
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    // the notification map to use for storing notifications
    private val downloadNotificationsMap = mutableMapOf<Int, DownloadNotification>()

    // the notification builder map to use for storing notification builders
    private val downloadNotificationsBuilderMap = mutableMapOf<Int, NotificationCompat.Builder>()

    // the notification exclude set to use for excluding notifications
    private val downloadNotificationExcludeSet = mutableSetOf<Int>()

    // The notification action to use for the notification manager
    override val notificationManagerAction =
        "DEFAULT_FETCH2_NOTIFICATION_MANAGER_ACTION_${System.currentTimeMillis()}"

    // The broadcast receiver to use for the notification manager
    override val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            // Handle the notification action triggered
            onDownloadNotificationActionTriggered(
                context = context,
                intent = intent,
                fetchNotificationManager = this@DefaultFetchNotificationManager
            )
        }
    }

    init {
        // Initialize the notification manager
        initialize()
    }

    /**
     * Initializes the notification manager.
     */
    private fun initialize() {
        // Register the broadcast receiver
        registerBroadcastReceiver()
        // Create the notification channels if needed
        createNotificationChannels(context, notificationManager)
    }

    /**
     * Registers the broadcast receiver for the notification manager.
     */
    override fun registerBroadcastReceiver() {
        // Register the broadcast receiver
        context.registerReceiver(broadcastReceiver, IntentFilter(notificationManagerAction))
    }

    /**
     * Unregisters the broadcast receiver for the notification manager.
     */
    override fun unregisterBroadcastReceiver() {
        // Unregister the broadcast receiver
        context.unregisterReceiver(broadcastReceiver)
    }

    /* Create notification channels if needed
        * @param context The application context
        * @param notificationManager The notification manager to use for creating notification channels
     */
    override fun createNotificationChannels(
        context: Context,
        notificationManager: NotificationManager
    ) {
        // Check if android version is more than or equal to Oreo (26)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Get the default notification channel id.
            val channelId = context.getString(R.string.fetch_notification_default_channel_id)
            // Get the default notification channel if it exists
            var channel: NotificationChannel? =
                notificationManager.getNotificationChannel(channelId)
            // Check if the default notification channel does not exist
            if (channel == null) {
                // Create the default notification channel
                val channelName =
                    context.getString(R.string.fetch_notification_default_channel_name)

                channel = NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_DEFAULT
                )

                // Create the default notification channel
                notificationManager.createNotificationChannel(channel)
            }
        }
    }

    // The notification channel id to use for the notification manager
    override fun getChannelId(notificationId: Int, context: Context): String {
        return context.getString(R.string.fetch_notification_default_channel_id)
    }

    /**
     * Updates the group summary notification with information from the list of [downloadNotifications].
     *
     * This function updates the provided [notificationBuilder] to create a group summary notification
     * that aggregates information from the list of [downloadNotifications]. It builds an InboxStyle
     * notification that lists download details for each notification in the group. The [context] is used
     * for resource string retrieval.
     *
     * @param groupId The unique identifier of the group to which the notification belongs.
     * @param notificationBuilder The builder for the group summary notification.
     * @param downloadNotifications The list of download notifications in the group.
     * @param context The Android application context for resource string retrieval.
     * @return `true` if the group notification should be used, `false` otherwise.
     */
    override fun updateGroupSummaryNotification(
        groupId: Int,
        notificationBuilder: NotificationCompat.Builder,
        downloadNotifications: List<DownloadNotification>,
        context: Context
    ): Boolean {
        // Set the group notification style
        val style = NotificationCompat.InboxStyle()
        // Iterate through the download notifications
        for (downloadNotification in downloadNotifications) {
            // Get the notification title
            val contentTitle = getSubtitleText(context, downloadNotification)
            // Add the notification title to the group notification style
            style.addLine("${downloadNotification.total} $contentTitle")
        }

        // Set the group notification attributes
        notificationBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setSmallIcon(android.R.drawable.stat_sys_download_done)
            .setContentTitle(context.getString(R.string.fetch_notification_default_channel_name))
            .setContentText("")
            .setStyle(style)
            .setOnlyAlertOnce(true)
            .setSilent(true)
            .setGroup(groupId.toString())
            .setGroupSummary(true)
        return false
    }

    /**
     * Updates the notification builder based on the provided DownloadNotification.
     *
     * This function updates the content and behavior of a notification using the [notificationBuilder]
     * based on the information provided in the [downloadNotification]. It configures the notification's
     * title, content, progress, and actions (e.g., pause, resume, cancel) according to the status of the
     * download.
     *
     * @param notificationBuilder The NotificationCompat.Builder to be updated.
     * @param downloadNotification The DownloadNotification object containing information about the download status.
     * @param context The context in which the notification is being updated.
     */
    override fun updateNotification(
        notificationBuilder: NotificationCompat.Builder,
        downloadNotification: DownloadNotification,
        context: Context
    ) {
        // Get the notification small icon based on the download status
        val smallIcon = if (downloadNotification.isDownloading) {
            android.R.drawable.stat_sys_download
        } else {
            android.R.drawable.stat_sys_download_done
        }
        // Check if the notification is ongoing or paused
        val isOngoingNotification =
            downloadNotification.isOnGoingNotification || downloadNotification.isPaused
        // Set the notification attributes
        notificationBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setSmallIcon(smallIcon)
            .setContentTitle(downloadNotification.title)
            .setContentText(getSubtitleText(context, downloadNotification))
            .setOngoing(isOngoingNotification)
            .setGroup(downloadNotification.groupId.toString())
            .setGroupSummary(false)
            .setSilent(true)

        // Check if the download notification is completed or failed
        if (downloadNotification.isFailed || downloadNotification.isCompleted) {
            // Set the notification to auto cancel
            notificationBuilder.setProgress(0, 0, false)
        } else {
            // Set the notification progress
            val progressIndeterminate = downloadNotification.progressIndeterminate
            // Get the max progress
            val maxProgress = if (downloadNotification.progressIndeterminate) 0 else 100
            // Get the progress
            val progress =
                if (downloadNotification.progress < 0) 0 else downloadNotification.progress
            // Set the notification progress
            notificationBuilder.setProgress(maxProgress, progress, progressIndeterminate)
        }
        // Set the notification actions
        when {
            downloadNotification.isDownloading -> {
                // if the download is ongoing, set the pause and cancel actions
                notificationBuilder
                    .setTimeoutAfter(getNotificationTimeOutMillis())
                    .applyIf(canPauseDownloads) {
                        addAction(
                            R.drawable.fetch_notification_pause,
                            context.getString(R.string.fetch_notification_download_pause),
                            getActionPendingIntent(downloadNotification, PAUSE)
                        )
                    }
                    .applyIf(canCancelDownloads) {
                        addAction(
                            R.drawable.fetch_notification_cancel,
                            context.getString(R.string.fetch_notification_download_cancel),
                            getActionPendingIntent(downloadNotification, CANCEL)
                        )
                    }
            }

            downloadNotification.isPaused -> {
                // if the download is paused, set the resume and cancel actions
                notificationBuilder
                    .setTimeoutAfter(getNotificationTimeOutMillis())
                    .applyIf(canPauseDownloads) {
                        addAction(
                            R.drawable.fetch_notification_resume,
                            context.getString(R.string.fetch_notification_download_resume),
                            getActionPendingIntent(downloadNotification, RESUME)
                        )
                    }
                    .applyIf(canCancelDownloads) {
                        addAction(
                            R.drawable.fetch_notification_cancel,
                            context.getString(R.string.fetch_notification_download_cancel),
                            getActionPendingIntent(downloadNotification, CANCEL)
                        )
                    }
            }

            downloadNotification.isQueued -> {
                // if the download is queued, set the cancel action
                notificationBuilder.setTimeoutAfter(getNotificationTimeOutMillis())
            }
        }
    }

    /**
     * Creates a PendingIntent for a specific action (e.g., cancel, delete, etc.) on a single download notification.
     *
     * This function generates a [PendingIntent] for actions on a specific download notification, such as
     * canceling, deleting, resuming, pausing, or retrying the download. The [downloadNotification] parameter
     * represents the [DownloadNotification] object to which the action should be applied, and [actionType]
     * specifies the type of action to be performed.
     *
     * @param downloadNotification The specific [DownloadNotification] object on which the action should be performed.
     * @param actionType The type of action to be executed on the download notification (e.g., CANCEL, DELETE, etc.).
     * @return A [PendingIntent] for triggering the specified action on the download notification.
     */
    override fun getActionPendingIntent(
        downloadNotification: DownloadNotification,
        actionType: DownloadNotification.ActionType
    ): PendingIntent {
        // Synchronize the download notifications map to ensure thread safety
        synchronized(downloadNotificationsMap) {
            val intent = Intent(notificationManagerAction)
            intent.putExtra(EXTRA_NAMESPACE, downloadNotification.namespace)
            intent.putExtra(EXTRA_DOWNLOAD_ID, downloadNotification.notificationId)
            intent.putExtra(EXTRA_NOTIFICATION_ID, downloadNotification.notificationId)
            intent.putExtra(EXTRA_GROUP_ACTION, false)
            intent.putExtra(EXTRA_NOTIFICATION_GROUP_ID, downloadNotification.groupId)
            // Get the action type
            val action = when (actionType) {
                CANCEL -> ACTION_TYPE_CANCEL
                DELETE -> ACTION_TYPE_DELETE
                RESUME -> ACTION_TYPE_RESUME
                PAUSE -> ACTION_TYPE_PAUSE
                RETRY -> ACTION_TYPE_RETRY
                else -> ACTION_TYPE_INVALID
            }
            intent.putExtra(EXTRA_ACTION_TYPE, action)
            // Return the pending intent
            return PendingIntent.getBroadcast(
                context,
                downloadNotification.notificationId + action,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )
        }
    }

    /**
     * Creates a PendingIntent for group action (e.g., cancel all, delete all, etc.) on a group of
     * download notifications.
     *
     * This function generates a PendingIntent for group actions such as canceling all downloads,
     * deleting all downloads, resuming all downloads, or pausing all downloads. It allows these
     * actions to be applied to a group of download notifications with the specified [groupId].
     * The [downloadNotifications] parameter is a list of DownloadNotification objects belonging to the
     * same group. The [actionType] determines the type of group action to be performed.
     *
     * @param groupId The unique identifier of the group of download notifications.
     * @param downloadNotifications List of DownloadNotification objects in the group.
     * @param actionType The type of group action to be performed (e.g., CANCEL_ALL, DELETE_ALL, etc.).
     * @return A PendingIntent for triggering the specified group action on the download notifications.
     */
    override fun getGroupActionPendingIntent(
        groupId: Int,
        downloadNotifications: List<DownloadNotification>,
        actionType: DownloadNotification.ActionType
    ): PendingIntent {
        // Synchronize the download notifications map to ensure thread safety
        synchronized(downloadNotificationsMap) {
            val intent = Intent(notificationManagerAction)
            intent.putExtra(EXTRA_NOTIFICATION_GROUP_ID, groupId)
            intent.putExtra(EXTRA_DOWNLOAD_NOTIFICATIONS, ArrayList(downloadNotifications))
            intent.putExtra(EXTRA_GROUP_ACTION, true)
            val action = when (actionType) {
                CANCEL_ALL -> ACTION_TYPE_CANCEL_ALL
                DELETE_ALL -> ACTION_TYPE_DELETE_ALL
                RESUME_ALL -> ACTION_TYPE_RESUME_ALL
                PAUSE_ALL -> ACTION_TYPE_PAUSE_ALL
                RETRY_ALL -> ACTION_TYPE_RETRY_ALL
                else -> ACTION_TYPE_INVALID
            }
            intent.putExtra(EXTRA_ACTION_TYPE, action)
            // Return the pending intent
            return PendingIntent.getBroadcast(
                context,
                groupId + action,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )
        }
    }

    /**
     * Cancels a specific download notification.
     *
     * This function cancels a notification with the given [notificationId]. It also removes the
     * associated entry from the `downloadNotificationsBuilderMap` and `downloadNotificationExcludeSet`.
     * If the notification is found in the `downloadNotificationsMap`, it's also removed from that map,
     * and the user is notified about the changes within its associated group.
     *
     * @param notificationId The unique identifier of the notification to be canceled.
     * @see downloadNotificationsMap
     * @see downloadNotificationsBuilderMap
     * @see downloadNotificationExcludeSet
     * @see notify
     */
    override fun cancelNotification(notificationId: Int) {
        // Synchronize the download notifications map to ensure thread safety
        synchronized(downloadNotificationsMap) {
            // Cancel the notification
            notificationManager.cancel(notificationId)
            // Remove the notification from the notification builder map
            downloadNotificationsBuilderMap.remove(notificationId)
            // Remove the notification from the exclude set
            downloadNotificationExcludeSet.remove(notificationId)
            val downloadNotification = downloadNotificationsMap[notificationId]
            if (downloadNotification != null) {
                // Remove the notification from the download notifications map
                downloadNotificationsMap.remove(notificationId)
                // Notify the user about the changes by removing the notification from the group
                notify(downloadNotification.groupId)
            }
        }
    }

    /**
     * Cancels ongoing download notifications for incomplete downloads.
     *
     * This function iterates through the download notifications and cancels ongoing notifications
     * associated with incomplete downloads (i.e., neither failed nor completed). It removes the
     * corresponding entries from internal data structures and notifies the user about the changes.
     * Ongoing notifications for failed or completed downloads are not canceled.
     *
     * @see downloadNotificationsMap
     * @see downloadNotificationsBuilderMap
     * @see downloadNotificationExcludeSet
     * @see notify
     */
    override fun cancelOngoingNotifications() {
        // Synchronize the download notifications map to ensure thread safety
        synchronized(downloadNotificationsMap) {
            // Get the download notifications iterator
            val iterator = downloadNotificationsMap.values.iterator()
            var downloadNotification: DownloadNotification
            // Iterate through the download notifications
            while (iterator.hasNext()) {
                // Get the next download notification
                downloadNotification = iterator.next()
                // Check if the download notification is not completed or failed
                if (!downloadNotification.isFailed && !downloadNotification.isCompleted) {
                    // Cancel the notification
                    notificationManager.cancel(downloadNotification.notificationId)
                    // Remove the notification from the notification builder map
                    downloadNotificationsBuilderMap.remove(downloadNotification.notificationId)
                    // Remove the notification from the exclude set
                    downloadNotificationExcludeSet.remove(downloadNotification.notificationId)
                    iterator.remove()
                    // Notify the user about the changes by removing the notification from the group
                    notify(downloadNotification.groupId)
                }
            }
        }
    }

    /**
     * Notifies the user about download status changes in a specified notification group.
     *
     * @param groupId The ID of the notification group to notify.
     *
     * This function is responsible for handling and updating notifications related to download status changes
     * within a specific group. It retrieves the download notifications associated with the given group, updates
     * the group summary notification if necessary, and notifies the user about individual download status changes.
     *
     * @see getNotificationBuilder
     * @see updateGroupSummaryNotification
     * @see shouldUpdateNotification
     * @see downloadNotificationExcludeSet
     */
    override fun notify(groupId: Int) {
        // Synchronize the download notifications map to ensure thread safety
        synchronized(downloadNotificationsMap) {
            // Get the grouped download notifications from the download notifications map based on the group id
            val groupedDownloadNotifications =
                downloadNotificationsMap.values.filter { it.groupId == groupId }
            // Get the group summary notification builder
            val groupSummaryNotificationBuilder = getNotificationBuilder(groupId, groupId)
            // Update the group summary notification
            val useGroupNotification = updateGroupSummaryNotification(
                groupId,
                groupSummaryNotificationBuilder,
                groupedDownloadNotifications,
                context
            )
            var notificationId: Int
            var notificationBuilder: NotificationCompat.Builder

            // Iterate through the grouped download notifications
            for (downloadNotification in groupedDownloadNotifications) {
                // Check if the download notification should be updated
                if (shouldUpdateNotification(downloadNotification)) {
                    // Get the notification id and notification builder for the download notification
                    notificationId = downloadNotification.notificationId
                    notificationBuilder = getNotificationBuilder(notificationId, groupId)

                    // Update the notification builder
                    updateNotification(notificationBuilder, downloadNotification, context)
                    // Notify the notification manager
                    notificationManager.notify(notificationId, notificationBuilder.build())
                    when (downloadNotification.status) {
                        Status.COMPLETED,
                        Status.FAILED -> {
                            // Add the download notification to the exclude set when it is completed or failed
                            downloadNotificationExcludeSet.add(downloadNotification.notificationId)
                        }

                        else -> {}
                    }
                }
            }

            // Check if the group notification should be used
            if (useGroupNotification) {
                // Notify the notification manager for the whole group
                notificationManager.notify(groupId, groupSummaryNotificationBuilder.build())
            }
        }
    }

    /**
     * Determines whether a download notification should be updated.
     *
     * @param downloadNotification The download notification to evaluate.
     * @return `true` if the notification should be updated, `false` otherwise.
     *
     * This function checks if the given download notification's ID is not present in the
     * exclusion set, indicating that it should be updated. Notifications that are part of this exclusion set
     * are skipped from updates, typically to prevent constant updates for certain notification types.
     */
    override fun shouldUpdateNotification(downloadNotification: DownloadNotification): Boolean {
        return !downloadNotificationExcludeSet.contains(downloadNotification.notificationId)
    }

    /**
     * Determines whether a download notification should be canceled.
     *
     * @param downloadNotification The download notification to evaluate.
     * @return `true` if the notification should be canceled, `false` otherwise.
     *
     * This function checks the status of the given download notification and returns `true` if the status
     * indicates that the notification should be canceled. Notifications with statuses of "CANCELLED," "REMOVED,"
     * or "DELETED" are candidates for cancellation.
     */
    override fun shouldCancelNotification(downloadNotification: DownloadNotification): Boolean {
        return downloadNotification.status == Status.CANCELLED
                || downloadNotification.status == Status.REMOVED
                || downloadNotification.status == Status.DELETED
    }

    /**
     * Posts a download update to manage and display download notifications.
     *
     * @param download The download object containing the download information.
     * @return `true` if the download update was successfully processed, `false` otherwise.
     *
     * This function is responsible for updating and managing download notifications. It maintains a mapping
     * of download notifications and their associated information, such as download status, progress, ETA, etc.
     * If the number of active download notifications exceeds a certain threshold, older notifications are cleared
     * to prevent excessive memory usage. The function also handles the display and removal of notifications based on
     * the download's status, progress, and other criteria.
     */
    override fun postDownloadUpdate(download: Download): Boolean {
        // Synchronize the download notifications map to ensure thread safety
        return synchronized(downloadNotificationsMap) {
            // Check if the download notification map size is greater than 50
            if (downloadNotificationsMap.size > 50) {
                // Clear the download notification map
                downloadNotificationsBuilderMap.clear()
                downloadNotificationsMap.clear()
            }
            // Get or Create the download notification
            val downloadNotification =
                downloadNotificationsMap[download.id] ?: DownloadNotification()
            // Sets the notification attributes
            downloadNotification.status = download.status
            downloadNotification.progress = download.progress
            downloadNotification.notificationId = download.id
            downloadNotification.groupId = download.group
            downloadNotification.etaInMilliSeconds = download.etaInMilliSeconds
            downloadNotification.downloadedBytesPerSecond = download.downloadedBytesPerSecond
            downloadNotification.total = download.total
            downloadNotification.downloaded = download.downloaded
            downloadNotification.namespace = download.namespace
            downloadNotification.title = getDownloadNotificationTitle(download)

            // Put the download notification in the map
            downloadNotificationsMap[download.id] = downloadNotification
            // Check if the download notification is completed or failed
            if (downloadNotificationExcludeSet.contains(downloadNotification.notificationId)
                && !downloadNotification.isFailed && !downloadNotification.isCompleted
            ) {
                // Remove the download notification from the exclude set
                downloadNotificationExcludeSet.remove(downloadNotification.notificationId)
            }

            // Check if the download notification is cancelled or should be cancelled
            if (downloadNotification.isCancelledNotification || shouldCancelNotification(
                    downloadNotification
                )
            ) {
                // Cancel the download notification
                cancelNotification(downloadNotification.notificationId)
            } else {
                // Else notify the download notification
                notify(download.group)
            }
            true
        }
    }

    /**
     * Gets a NotificationCompat.Builder for a specific download notification.
     *
     * @param notificationId The unique identifier for the notification.
     * @param groupId The group identifier for grouping notifications.
     * @return A NotificationCompat.Builder for the download notification.
     *
     * This function retrieves or creates a notification builder for a specific notification using its unique
     * identifier. It allows customization of various notification attributes such as group, style, progress,
     * content title, content text, content intent, group summary, and more. It also ensures that only one
     * builder is created for each unique notification identifier.
     */
    @SuppressLint("RestrictedApi")
    override fun getNotificationBuilder(
        notificationId: Int,
        groupId: Int
    ): NotificationCompat.Builder {
        // Synchronize the download notifications map to ensure thread safety
        synchronized(downloadNotificationsMap) {
            // Get the notification builder for the notification id or create a new one if it does not exist
            val notificationBuilder = downloadNotificationsBuilderMap[notificationId]
                ?: NotificationCompat.Builder(context, getChannelId(notificationId, context))
            // Store the notification builder in the map
            downloadNotificationsBuilderMap[notificationId] = notificationBuilder

            // set the notification builder attributes
            notificationBuilder
                .setGroup(notificationId.toString())
                .setStyle(null)
                .setProgress(0, 0, false)
                .setContentTitle(null)
                .setContentText(null)
                .setContentIntent(null)
                .setGroupSummary(false)
                .setOngoing(false)
                .setGroup(groupId.toString())
                .setOnlyAlertOnce(true)
                .setSilent(true)
                .setSmallIcon(android.R.drawable.stat_sys_download_done)
                .mActions.clear()

            // Return the notification builder
            return notificationBuilder
        }
    }

    /**
     * Gets the notification timeout value in milliseconds.
     *
     * @return The notification timeout value.
     */
    override fun getNotificationTimeOutMillis(): Long {
        return 0
    }

    /**
     * Retrieves the Fetch instance associated with a specific namespace.
     *
     * @param namespace The namespace identifier.
     * @return The Fetch instance for the given namespace.
     */
    abstract override fun getFetchInstanceForNamespace(namespace: String): Fetch

    /**
     * Returns the title for a download notification.
     *
     * @param download The download for which the title is generated.
     * @return The title for the download notification.
     */
    override fun getDownloadNotificationTitle(download: Download): String {
        // Get the file name from the download based on the file uri or the download url
        return download.fileUri.lastPathSegment ?: Uri.parse(download.url).lastPathSegment
        ?: download.url
    }

    /**
     * Generates subtitle text for a download notification.
     *
     * @param context The Android context.
     * @param downloadNotification The download notification object.
     * @return The subtitle text for the notification.
     */
    override fun getSubtitleText(
        context: Context,
        downloadNotification: DownloadNotification
    ): String {
        // Return the subtitle text based on the download notification status
        return when {
            downloadNotification.isCompleted -> context.getString(R.string.fetch_notification_download_complete)
            downloadNotification.isFailed -> context.getString(R.string.fetch_notification_download_failed)
            downloadNotification.isPaused -> context.getString(R.string.fetch_notification_download_paused)
            downloadNotification.isQueued -> context.getString(R.string.fetch_notification_download_starting)
            downloadNotification.etaInMilliSeconds < 0 -> context.getString(R.string.fetch_notification_download_downloading)
            else -> getEtaText(context, downloadNotification.etaInMilliSeconds)
        }
    }

    /**
     * Converts ETA time in milliseconds to a formatted text.
     *
     * @param context The Android context.
     * @param etaInMilliSeconds The estimated time of arrival in milliseconds.
     * @return Formatted ETA text.
     */
    private fun getEtaText(context: Context, etaInMilliSeconds: Long): String {
        // Convert to seconds
        var seconds = (etaInMilliSeconds / 1000)
        // Convert to hours
        val hours = (seconds / 3600)
        // Remove the hours from the seconds
        seconds -= (hours * 3600)
        // Convert to minutes
        val minutes = (seconds / 60)
        // Remove the minutes from the seconds
        seconds -= (minutes * 60)
        // Return the formatted ETA text
        return when {
            hours > 0 -> context.getString(
                R.string.fetch_notification_download_eta_hrs,
                hours,
                minutes,
                seconds
            )

            minutes > 0 -> context.getString(
                R.string.fetch_notification_download_eta_min,
                minutes,
                seconds
            )

            else -> context.getString(R.string.fetch_notification_download_eta_sec, seconds)
        }
    }
}