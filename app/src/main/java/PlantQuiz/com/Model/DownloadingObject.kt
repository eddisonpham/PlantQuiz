package PlantQuiz.com.Model

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class DownloadingObject {
    @Throws(IOException::class)
    fun downloadJSONDataFromLink(link: String): String{
        val stringBuilder: StringBuilder = StringBuilder()

        val url = URL(link)
        val urlConnection = url.openConnection() as HttpURLConnection
        try{
            val bufferedInputString: BufferedInputStream =
                BufferedInputStream(urlConnection.inputStream)
            val bufferedReader: BufferedReader =
                BufferedReader(InputStreamReader(bufferedInputString))
            //temporary string to hold each line read from the reader
            var inputLine: String?
            inputLine = bufferedReader.readLine()
            while (inputLine!=null){
                stringBuilder.append(inputLine)
                inputLine = bufferedReader.readLine()
            }
        }finally{
            // regardless of success or failure, we will disconnect from the URLConnection
            urlConnection.disconnect()
        }
        return stringBuilder.toString()
    }

    fun downloadPicture(pictureName: String?): Bitmap?{

        var bitmap: Bitmap? = null
        val pictureLink =  PLANTPLACES_COM + "/photo/$pictureName"
        val pictureUrl = URL(pictureLink)
        val inputStream = pictureUrl.openConnection().getInputStream()
        if (inputStream != null){
            bitmap = BitmapFactory.decodeStream(inputStream)
        }
        return bitmap
    }

    companion object{
        val PLANTPLACES_COM = "http://www.plantplaces.com"
    }
}