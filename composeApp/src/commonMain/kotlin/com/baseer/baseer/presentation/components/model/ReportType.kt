package com.baseer.baseer.presentation.components.model

import baseer.composeapp.generated.resources.*
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

sealed class ReportType(
    val id: String,
    val titleRes: StringResource,
    val icon: DrawableResource,
    val requiresPlateNumber: Boolean = false,
    val requiresDescription: Boolean = true
) {
    data object Fire : ReportType(
        id = "0",
        titleRes = Res.string.report_type_fire,
        icon = Res.drawable.ic_fire
    )

    data object Conflict : ReportType(
        id = "1",
        titleRes = Res.string.report_type_conflict,
        icon = Res.drawable.ic_humens
    )

    data object BrokenCar : ReportType(
        id = "3",
        titleRes = Res.string.report_type_broken_car,
        icon = Res.drawable.ic_car,
        requiresPlateNumber = true
    )

    data object RecklessDriver : ReportType(
        id = "4",
        titleRes = Res.string.report_type_reckless_driver,
        icon = Res.drawable.ic_warning,
        requiresPlateNumber = true
    )

    data object Begging : ReportType(
        id = "2",
        titleRes = Res.string.report_type_begging,
        icon = Res.drawable.ic_slave
    )

    data object SuspectedDrugs : ReportType(
        id = "5",
        titleRes = Res.string.report_type_suspected_drugs,
        icon = Res.drawable.ic_drug
    )

    data object Harassment : ReportType(
        id = "6",
        titleRes = Res.string.report_type_harassment,
        icon = Res.drawable.ic_shield
    )

    data object Theft : ReportType(
        id = "7",
        titleRes = Res.string.report_type_theft,
        icon = Res.drawable.ic_bag
    )

    companion object {
        val all: List<ReportType> = listOf(
            Fire, Conflict, BrokenCar, RecklessDriver,
            Begging, SuspectedDrugs, Harassment, Theft
        )

        fun fromId(id: String): ReportType? = all.find { it.id == id }
    }
}