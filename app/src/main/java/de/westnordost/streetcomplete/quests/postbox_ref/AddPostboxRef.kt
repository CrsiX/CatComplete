package de.westnordost.streetcomplete.quests.postbox_ref

import de.westnordost.streetcomplete.R
import de.westnordost.streetcomplete.data.quest.NoCountriesExcept
import de.westnordost.streetcomplete.data.osm.osmquest.OsmFilterQuestType
import de.westnordost.streetcomplete.data.osm.changes.StringMapChangesBuilder
import de.westnordost.streetcomplete.ktx.asSingleArray
import de.westnordost.streetcomplete.ktx.containsAny

class AddPostboxRef : OsmFilterQuestType<PostboxRefAnswer>() {

    override val elementFilter = "nodes with amenity = post_box and !ref and !ref:signed"

    override val icon = R.drawable.ic_quest_mail_ref
    override val commitMessage = "Add postbox refs"
    override val wikiLink = "Tag:amenity=post_box"
    override val isDeleteElementEnabled = true

    // source: https://commons.wikimedia.org/wiki/Category:Post_boxes_by_country
    override val enabledInCountries = NoCountriesExcept(
            "FR", "GB", "GG", "IM", "JE", "MT", "IE", "SG", "CZ", "SK", "CH", "US"
    )

    override fun getTitleArgs(tags: Map<String, String>, featureName: Lazy<String?>): Array<String> =
        (tags["name"] ?: tags["brand"] ?: tags["operator"]).asSingleArray()

    override fun getTitle(tags: Map<String, String>): Int {
        val hasName = tags.keys.containsAny(listOf("name","brand","operator"))
        return if (hasName) R.string.quest_postboxRef_name_title
               else         R.string.quest_postboxRef_title
    }

    override fun createForm() = AddPostboxRefForm()

    override fun applyAnswerTo(answer: PostboxRefAnswer, changes: StringMapChangesBuilder) {
        when(answer) {
            is NoRefVisible -> changes.add("ref:signed", "no")
            is Ref ->          changes.add("ref", answer.ref)
        }
    }
}
