//colors;
//it - gray
//hm - orange
//tm - yellow
//bse - blue
//canteens kakasjay - green
//faculties - darker color depende no niya department
//library, fishfishes, courts, dean, hostel, student center, ssc - red
//parkingan - brown
//balay ti uttogs - pink
//reserve-able lang jay it buildings, dadduma macklick ngem info lang agparang
//pag joinen yunto man agijay buildings ta maifill colors tu langnen

package com.example.dbtest.map

import android.graphics.Color
import android.graphics.PointF

data class Building(
    val id: String,
    val label: String,
    val points: List<PointF>,
    // Null = use SiteMapView's default fill color. Set this to give a
    // building/room group its own color (e.g. IT buildings are gray).
    val fillColor: Int? = null
)


object CampusBuildings {

    // Bumped up from the default alpha (90) so IT buildings read as clearly
    // gray instead of a faint tint. SiteMapView auto-darkens this when an
    // IT building is pressed/selected, so no separate "pressed" color needed.
    private val IT_GRAY = Color.argb(170, 105, 105, 105)

    // Same treatment for the HM building group, per the "hm - orange" scheme
    // noted at the top of this file. Matches hex #FF7900.
    private val HM_ORANGE = Color.argb(170, 255, 121, 0)

    //bot right, bot left, top left, top right
    val OTHER_BUILDINGS: List<Building> = listOf(
        Building("building_1", "CR", pts(247,156, 226,156, 226,132, 247,132)),
        Building("building_2", "CR", pts(437,99, 463,98, 464,134, 438,135)),
        Building("building_3a", "Building 3", pts(556,89, 474,88, 473,128, 558,129)),
        Building("building_3b", "Building 3(2)", pts(594,45, 549,51, 560,108, 564,125, 608,118)),
        Building("building_4", "Traning Center", pts(621,147, 618,42, 757,37, 761,142)),
        Building("building_5", "Fishery", pts(993,147, 909,147, 909,44, 993,44)),
        Building("building_6a", "Building 6 - Room 1", pts(283,176, 240,176, 240,131, 283,131)),
        Building("building_6b", "Building 6 - Room 2", pts(325,176, 283,176, 283,131, 325,131)),
        Building("building_6c", "Building 6 - Room 3", pts(368,176, 325,176, 325,131, 368,131)),
        Building("building_8", "Registrar", pts(928,218, 867,218, 867,139, 928,139)),
        Building("building_9", "Parking Area", pts(1263,222, 1088,222, 1088,107, 1263,107)),
        Building("building_10", "Gray gym", pts(134,327, 17,327, 17,158, 134,158)),
        Building("building_11", "Open Court", pts(365,346, 265,346, 265,194, 365,194)),
        Building("building_14", "Building 14", pts(795,270, 757,269, 758,241, 796,242)),
        Building("building_15", "Building 15", pts(835,266, 807,266, 807,246, 835,246)),
        Building("building_16", "Building 16", pts(855,264, 830,264, 830,245, 855,245)),
        Building("building_17", "Building 17", pts(866,294, 863,236, 905,234, 908,292)),
        Building("building_18a", "Building 18 - Room 1", pts(163,294, 162,255, 206,255, 207,294)),
        Building("building_18b", "Building 18 - Room 2", pts(163,334, 163,294, 207,294, 208,334)),
        Building("building_18c", "Building 18 - Room 3", pts(164,373, 163,334, 208,334, 209,373)),
        Building("building_20", "School Van Parking Area", pts(750,356, 713,356, 713,325, 750,325)),
        Building("building_21", "Building 21", pts(814,330, 778,330, 778,283, 814,283)),
        Building("building_22", "Hostel", pts(1002,335, 999,271, 1037,270, 1040,334)),
        Building("building_23", "Guard's House Main", pts(1206,383, 1178,383, 1178,356, 1206,356)),
        Building("building_25", "Guard's House", pts(666,492, 646,492, 646,471, 666,471)),
        Building("building_26", "Ancient", pts(763,464, 679,464, 679,402, 763,402)),
    )

    // -----------------------------------------------------------------
    // IT BUILDING COMPLEX - kept separate from OTHER_BUILDINGS above so
    // its polygons/labels are easy to find and debug on their own.
    // Building 12 + 13 = "IT Building 1" (rooms 1A/1B, comp lab, internet
    // room). Building 19 = "IT Building 2" (rooms 2A/2B, faculty).
    // All filled gray to match the "it - gray" scheme noted above.
    // -----------------------------------------------------------------
    val IT_BUILDINGS: List<Building> = listOf(
        // IT Building 1 (Building 12 side)
        Building("building_12a", "IT 1B Room", pts(427,218, 427,181, 473,181, 474,218), fillColor = IT_GRAY),

        // IT Building 1 (Building 13 side)
        Building("building_13a", "IT 1A Room", pts(538,198, 615,198, 615,229, 538,229), fillColor = IT_GRAY),
        Building("building_13b", "Computer Lab/IT4", pts(587,229, 615,229, 615,280, 587,280), fillColor = IT_GRAY),
        Building("building_13c", "Internet Room/IT3", pts(587,280, 615,280, 615,330, 587,330), fillColor = IT_GRAY),

        // IT Building 2 (Building 19)
        Building("building_19a", "IT 2B Room", pts(533,373, 533,340, 570,338, 570,373), fillColor = IT_GRAY),
        Building("building_19b", "IT 2A Room", pts(570,372, 570,338, 610,338, 610,372), fillColor = IT_GRAY),
        Building("building_19c", "IT Faculty", pts(611,372, 611,347, 637,349, 639,371), fillColor = IT_GRAY),
    )

    // -----------------------------------------------------------------
    // HM BUILDING GROUP - separated out the same way as IT_BUILDINGS,
    // filled orange per the "hm - orange" scheme noted at the top.
    // -----------------------------------------------------------------
    val HM_BUILDINGS: List<Building> = listOf(
        Building("building_7", "Building 7", pts(590,195, 524,195, 524,150, 590,150), fillColor = HM_ORANGE),
        Building("building_12b", "Building 12 - Room 2", pts(427,255, 427,218, 474,218, 474,255), fillColor = HM_ORANGE),
        Building("building_12c", "Building 12 - Room 3", pts(427,293, 427,255, 474,255, 475,293), fillColor = HM_ORANGE),
        Building("building_12d", "Building 12 - Room 4", pts(427,330, 427,293, 475,293, 475,330), fillColor = HM_ORANGE),
        Building("building_12e", "Faculty", pts(427,368, 427,330, 475,330, 476,368), fillColor = HM_ORANGE),
        Building("building_24a", "South - CR 1", pts(333,476, 308,476, 308,416, 333,416), fillColor = HM_ORANGE),
        Building("building_24b", "South - Room 1", pts(389,476, 333,476, 333,416, 389,416), fillColor = HM_ORANGE),
        Building("building_24c", "South - Room 2", pts(445,476, 389,476, 389,416, 445,416), fillColor = HM_ORANGE),
        Building("building_24d", "South - Room 3", pts(502,476, 445,476, 445,416, 502,416), fillColor = HM_ORANGE),
        Building("building_24e", "South - Room 4", pts(558,476, 502,476, 502,416, 558,416), fillColor = HM_ORANGE),
        Building("building_24f", "South - CR 2", pts(583,476, 558,476, 558,416, 583,416), fillColor = HM_ORANGE),
    )

    val ALL: List<Building> = OTHER_BUILDINGS + IT_BUILDINGS + HM_BUILDINGS

    private fun pts(vararg v: Int): List<PointF> {
        val list = mutableListOf<PointF>()
        var i = 0
        while (i < v.size) {
            list.add(PointF(v[i].toFloat(), v[i + 1].toFloat()))
            i += 2
        }
        return list
    }
}