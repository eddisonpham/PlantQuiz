package PlantQuiz.com.Model

import android.util.Log
import org.json.JSONArray
import org.json.JSONObject

class ParsePlantUtility {
    fun parsePlantObjectFromJSONData():List<Plant>?{

        var allPlantObject: ArrayList<Plant> = ArrayList()

        var downloadingObject = DownloadingObject()
        var topLevelPlantJSONData = downloadingObject
            .downloadJSONDataFromLink("https://plantplaces.com/perl/mobile/flashcard.pl")
        var topLevelJSONObject: JSONObject = JSONObject(topLevelPlantJSONData)
        var plantsObjectArray: JSONArray = topLevelJSONObject.getJSONArray("value")
        var index: Int = 0
        while (index < plantsObjectArray.length()){

            var plantObject: Plant = Plant()
            var jsonObject = plantsObjectArray.getJSONObject(index)

            with (jsonObject){
                plantObject.genus = getString("genus")
                plantObject.species = getString("species")
                plantObject.cultivar = getString("cultivar")
                plantObject.common = getString("common")
                plantObject.pictureName = getString("picture_Name")
                plantObject.description = getString("description")
                plantObject.difficulty = getInt("difficulty")
                plantObject.id = getInt("id")
            }
            allPlantObject.add(plantObject)
            index++
        }
        return allPlantObject
    }
}