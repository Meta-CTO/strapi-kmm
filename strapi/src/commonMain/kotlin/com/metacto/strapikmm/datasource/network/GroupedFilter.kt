package com.metacto.strapikmm.datasource.network

data class GroupedFilter(
    val key: String,
    val value: String,
    val type: FilterType,
)

enum class FilterType(val value: String) {
    EQUALS("eq"),
    EQUALS_CASE_INSENSITIVE("eqi"),
    NOT_EQUALS("ne"),
    NOT_EQUALS_CASE_INSENSITIVE("nei"),
    GREATER_THAN("gt"),
    GREATER_THAN_OR_EQUAL("gte"),
    LESS_THAN("lt"),
    LESS_THAN_OR_EQUAL("lte"),
    CONTAINS("contains"),
    NOT_CONTAINS("ncontains"),
    NULL("null"),
    NOT_NULL("notNull"),
    STARTS_WITH("startsWith"),
    ENDS_WITH("endsWith"),
    STARTS_WITH_CASE_INSENSITIVE("startsWithi"),
    ENDS_WITH_CASE_INSENSITIVE("endsWithi"),
    CONTAINS_CASE_INSENSITIVE("containsi"),
    CONTAINS_CASE_SENSITIVE("containss"),
    NOT_CONTAINS_CASE_INSENSITIVE("notContainsi"),
    NOT_CONTAINS_CASE_SENSITIVE("notContainss"),
    INCLUDED_IN("in"),
    NOT_INCLUDED_IN("notIn"),
    BETWEEN("between"),
}