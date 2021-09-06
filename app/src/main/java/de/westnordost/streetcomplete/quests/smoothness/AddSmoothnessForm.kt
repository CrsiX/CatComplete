package de.westnordost.streetcomplete.quests.smoothness

import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import de.westnordost.streetcomplete.R
import de.westnordost.streetcomplete.quests.AImageListQuestAnswerFragment
import de.westnordost.streetcomplete.quests.OtherAnswer
import de.westnordost.streetcomplete.quests.surface.Surface
import de.westnordost.streetcomplete.quests.surface.asItem
import de.westnordost.streetcomplete.view.image_select.Item
import de.westnordost.streetcomplete.view.image_select.ItemViewHolder

class AddSmoothnessForm : AImageListQuestAnswerFragment<Smoothness, SmoothnessAnswer>() {

    override val otherAnswers = listOf(
        // add other answer "none is applicable" that asks whether the surface is correct
        //  remove surface on incorrect, ask for note if correct
        OtherAnswer(R.string.quest_smoothness_wrong_surface) { surfaceWrong() },
        OtherAnswer(R.string.quest_smoothness_obstacle) { showObstacleHint() }
    )

    val surfaceTag get() = osmElement!!.tags["surface"]

    val highwayTag get() = osmElement!!.tags["highway"]

    // show only answers that make sense for the tagged surface
    // 0-7 are excellent - impassible, ordered like SmoothnessAnswer
    override val items get() = when {
        listOf("asphalt", "paving_stones", "concrete", "metal").contains(surfaceTag) -> getAnswers(0,4)
        listOf("sett", "unhewn_cobblestone", "compacted", "grass_paver").contains(surfaceTag) -> getAnswers(2,6)
        listOf("gravel", "fine_gravel", "pebbles").contains(surfaceTag) -> getAnswers(3,6)
        listOf("dirt", "grass").contains(surfaceTag) -> getAnswers(3,7)
        else -> getAnswers(0,7)
    }

    override val itemsPerRow = 2

    override val moveFavoritesToFront = false

    override fun onClickOk(selectedItems: List<Smoothness>) {
        applyAnswer(SmoothnessValueAnswer(selectedItems.single().osmValue))
    }

    private fun showObstacleHint() {
        activity?.let { AlertDialog.Builder(it)
            .setMessage(R.string.quest_smoothness_obstacle_hint)
            .setPositiveButton(android.R.string.ok, null)
            .show()
        }
    }

    private fun getAnswers(minSmoothness: Int, maxSmoothness: Int): List<Item<Smoothness>> {
        val answers = mutableListOf<Item<Smoothness>>()
        // this is assuming array is sorted in the order of SmoothnessAnswer (and works, at least on my phone)
        for (smoothness in Smoothness.values().sliceArray(minSmoothness..maxSmoothness))
            answers.add(Item(smoothness, getImage(smoothness), getDescription(smoothness)))
        return answers
    }

    private fun getDescription(smoothness: Smoothness) = when (smoothness) {
        Smoothness.EXCELLENT -> R.string.quest_smoothness_excellent
        Smoothness.GOOD -> when (highwayTag) {
            in ALL_PATHS_EXCEPT_STEPS -> R.string.quest_smoothness_good
            else -> R.string.quest_smoothness_good
        }
        Smoothness.INTERMEDIATE -> when (highwayTag) {
            in ALL_PATHS_EXCEPT_STEPS -> R.string.quest_smoothness_intermediate_path
            else -> R.string.quest_smoothness_intermediate_road
        }
        Smoothness.BAD -> when (highwayTag) {
            in ALL_PATHS_EXCEPT_STEPS -> R.string.quest_smoothness_bad_path
            else -> R.string.quest_smoothness_bad_road
        }
        Smoothness.VERY_BAD -> when (highwayTag) {
            in ALL_PATHS_EXCEPT_STEPS -> R.string.quest_smoothness_very_bad_path
            else -> R.string.quest_smoothness_very_bad_road
        }
        Smoothness.HORRIBLE -> when (highwayTag) {
            in ALL_PATHS_EXCEPT_STEPS -> R.string.quest_smoothness_horrible
            else -> R.string.quest_smoothness_horrible
        }
        Smoothness.VERY_HORRIBLE -> when (highwayTag) {
            in ALL_PATHS_EXCEPT_STEPS -> R.string.quest_smoothness_very_horrible
            else -> R.string.quest_smoothness_very_horrible
        }
        Smoothness.IMPASSABLE -> when (highwayTag) {
            in ALL_PATHS_EXCEPT_STEPS -> R.string.quest_smoothness_impassable
            else -> R.string.quest_smoothness_impassable
        }
    }

    private fun getImage(smoothness: Smoothness) = when (smoothness) {
        Smoothness.EXCELLENT -> when (surfaceTag) {
            "asphalt" -> R.drawable.surface_asphalt_excellent
            "paving_stones" -> R.drawable.surface_paving_stones_excellent
            else -> R.drawable.surface_paved_area
        }
        Smoothness.GOOD -> when (surfaceTag) {
            "asphalt", "concrete" -> R.drawable.surface_asphalt_good
            "paving_stones" -> R.drawable.surface_paving_stones_good
            "sett" -> R.drawable.surface_sett_stones_good
            else -> R.drawable.surface_paved_area
        }
        Smoothness.INTERMEDIATE -> when (surfaceTag) {
            "asphalt", "concrete" -> R.drawable.surface_asphalt_intermediate
            "paving_stones" -> R.drawable.surface_paving_stones_intermediate
            "sett" -> R.drawable.surface_sett_intermediate
            else -> R.drawable.surface_paved_area
        }
        Smoothness.BAD -> when (surfaceTag) {
            "asphalt", "concrete" -> R.drawable.surface_asphalt_bad
            "paving_stones" -> R.drawable.surface_paving_stones_bad
            "sett" -> R.drawable.surface_sett
            else -> R.drawable.surface_paved_area
        }
        Smoothness.VERY_BAD -> when (surfaceTag) {
            "asphalt", "concrete" -> R.drawable.surface_asphalt_very_bad
            "paving_stones" -> R.drawable.surface_paving_stones_very_bad
            "sett" -> R.drawable.surface_sett_very_bad
            else -> R.drawable.surface_paved_area
        }
        Smoothness.HORRIBLE -> when (surfaceTag) {
            "asphalt", "concrete" -> R.drawable.surface_asphalt
            "paving_stones" -> R.drawable.surface_paving_stones
            else -> R.drawable.surface_paved_area
        }
        Smoothness.VERY_HORRIBLE -> when (surfaceTag) {
            "asphalt", "concrete" -> R.drawable.surface_asphalt
            "paving_stones" -> R.drawable.surface_paving_stones
            else -> R.drawable.surface_paved_area
        }
        Smoothness.IMPASSABLE -> when (surfaceTag) {
            "asphalt", "concrete" -> R.drawable.surface_asphalt
            "paving_stones" -> R.drawable.surface_paving_stones
            else -> R.drawable.surface_paved_area
        }
    }

    private fun surfaceWrong() {
        // ask whether surface is correct (show current value, like house number quest)
        //  if correct, ask for note
        //  else remove surface

        val surfaceType = Surface.values().find { it.osmValue == surfaceTag }

        if (surfaceType != null) {
            showWrongSurfaceDialog(surfaceType)
        } else {
            // should not happen
            composeNote()
        }
    }

    private fun showWrongSurfaceDialog(surface: Surface) {
        val inflater = LayoutInflater.from(requireContext())
        val inner = inflater.inflate(R.layout.dialog_quest_address_no_housenumber, null, false)
        ItemViewHolder(inner.findViewById(R.id.item_view)).bind(surface.asItem())

        AlertDialog.Builder(requireContext())
            .setView(inner)
            .setPositiveButton(R.string.quest_generic_hasFeature_yes) { _, _ -> composeNote() }
            .setNegativeButton(R.string.quest_generic_hasFeature_no) { _, _ -> applyAnswer(
                WrongSurfaceAnswer
            ) }
            .show()
    }

}
