package PlantQuiz.com.Controller

import PlantQuiz.com.Model.DownloadingObject
import PlantQuiz.com.Model.ParsePlantUtility
import PlantQuiz.com.Model.Plant
import PlantQuiz.com.R
import PlantQuiz.com.databinding.ActivityMainBinding
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.google.android.material.floatingactionbutton.FloatingActionButton


class MainActivity : AppCompatActivity() {

    //--------------------------Instance Variables-----------------------------//

    var correctAnswerIndex: Int = 0
    var correctPlant: Plant? = null
    var numberOfTimesUserAnsweredCorrectly: Int = 0
    var numberOfTimesUserAnsweredIncorrectly: Int = 0

    //--------------------------View Declarations-----------------------------//

    private lateinit var binding: ActivityMainBinding
    private lateinit var imageTaken: ImageView
    private lateinit var btnOpenCamera: Button
    private lateinit var btnOpenPhotoGallery: Button
    private lateinit var button1: Button
    private lateinit var button2: Button
    private lateinit var button3: Button
    private lateinit var button4: Button
    private lateinit var textView: TextView
    private lateinit var btnNextPlant: FloatingActionButton
    private lateinit var tvWrongAnswers: TextView
    private lateinit var tvRightAnswers: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var grayScreen: LinearLayout

    //--------------------------Request IDs-----------------------------------//

    val OPEN_CAMERA_BUTTON_REQUEST_ID = 1
    val OPEN_GALLARY_BUTTON_REQUEST_ID = 2

    //--------------------------Camera/Photo Buttons--------------------------//

    private lateinit var cameraButton: Button
    private lateinit var photoButton: Button

    //--------------------------Start Program---------------------------------//

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_main)
        setSupportActionBar(binding.toolbar)

        cameraButton = findViewById(R.id.btnOpenCamera)
        photoButton = findViewById(R.id.btnOpenPhotoGallery)
        imageTaken = findViewById(R.id.imgTaken)
        btnOpenCamera = findViewById(R.id.btnOpenCamera)
        btnOpenPhotoGallery = findViewById(R.id.btnOpenPhotoGallery)
        button1 = findViewById(R.id.button1)
        button2 = findViewById(R.id.button2)
        button3 = findViewById(R.id.button3)
        button4 = findViewById(R.id.button4)
        textView = findViewById(R.id.textView)
        btnNextPlant = findViewById(R.id.btnNextPlant)
        tvWrongAnswers = findViewById(R.id.tvWrongAnswers)
        tvRightAnswers = findViewById(R.id.tvRightAnswers)
        progressBar = findViewById(R.id.progressBar)
        grayScreen = findViewById(R.id.grayScreen)

        setProgressBar(false)
        displayUIWidgets(false)

        YoYo.with(Techniques.Pulse)
            .duration(700)
            .repeat(5)
            .playOn(btnNextPlant);

        cameraButton.setOnClickListener(View.OnClickListener {//Opens Camera
            Toast.makeText(
                this,"Camera is clicked",Toast.LENGTH_SHORT).show()
            val cameraIntent = Intent(
                MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, OPEN_CAMERA_BUTTON_REQUEST_ID)
        })

        photoButton.setOnClickListener(View.OnClickListener { //Opens photo gallery
            Toast.makeText(
                this,"Photo Gallery is clicked",Toast.LENGTH_SHORT).show()
            val galleryIntent = Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(
                galleryIntent, OPEN_GALLARY_BUTTON_REQUEST_ID)
        })

        //See next plant
        btnNextPlant.setOnClickListener(View.OnClickListener {
            if(checkForInternetConnection()){
                setProgressBar(true)
                try{
                    val innerClassObject = DownloadingImageTask()
                    innerClassObject.execute()
                }catch (e:Exception){
                    e.printStackTrace()
                }
//
//                var gradientColors: IntArray = IntArray(2)
//                gradientColors.set(0, Color.parseColor("#FFFF66"))
//                gradientColors.set(1, Color.parseColor("#ff0008"))
//                var gradientDrawable: GradientDrawable = GradientDrawable(
//                    GradientDrawable.Orientation.TOP_BOTTOM,
//                    gradientColors
//                )
//                var convertedDipValue = dipToPixels(this@MainActivity, 50f)
//                gradientDrawable.setCornerRadius(convertedDipValue)
//                gradientDrawable.setStroke(5, Color.parseColor("#FFFFFF"))
                button1.setBackgroundColor(R.drawable.button_border)
                button2.setBackgroundColor(R.drawable.button_border)
                button3.setBackgroundColor(R.drawable.button_border)
                button4.setBackgroundColor(R.drawable.button_border)

            }
        })
    }

    private fun dipToPixels(context: Context, dipValue: Float): Float {
        val metrics: DisplayMetrics = context.getResources().getDisplayMetrics()
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics)
    }

    //--------------------------Android Life Cycle----------------------------//

    override fun onStart() {//Execute when start program
        super.onStart()
        Toast.makeText(
            this, "THE ONSTART METHOD IS CALLED", Toast.LENGTH_SHORT).show()
    }
    override fun onResume() {//Execute when user goes back into program or start
        super.onResume()
//        Toast.makeText(
//            this, "THE ONRESUME METHOD IS CALLED", Toast.LENGTH_SHORT).show()
        checkForInternetConnection()
    }

    override fun onPause() {//Executes when user taps overview button
        super.onPause()
        Toast.makeText(
            this, "THE ONPAUSE METHOD IS CALLED", Toast.LENGTH_SHORT).show()
    }
    override fun onStop() {//Executes when user taps home button
        super.onStop()
        Toast.makeText(
            this, "THE ONSTOP METHOD IS CALLED", Toast.LENGTH_SHORT).show()
    }
    override fun onDestroy() {//Executes when user taps arrow to exit program
        super.onDestroy()
        Toast.makeText(
            this, "THE ONDESTROY METHOD IS CALLED", Toast.LENGTH_SHORT).show()
    }
    override fun onRestart() {//Executes when program resets
        super.onRestart()
        Toast.makeText(
            this, "THE ONRESTART METHOD IS CALLED", Toast.LENGTH_SHORT).show()
    }

    //--------------------------Post Image On Screen----------------------------//

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == OPEN_CAMERA_BUTTON_REQUEST_ID){//Camera

            if(resultCode== RESULT_OK){
                val imageData = data?.getExtras()?.get("data") as Bitmap
                imageTaken.setImageBitmap(imageData)
            }

        }
        if (requestCode == OPEN_GALLARY_BUTTON_REQUEST_ID){//Gallery
            if (resultCode == RESULT_OK){
                val contentURI = data?.getData()
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver,
                contentURI)
                imageTaken.setImageBitmap(bitmap)
            }
        }
    }

    //--------------------------Buttons Functionalities----------------------------//

    fun button1isClicked(buttonView: View){
//        Toast.makeText(
//            this,"Button 1 is clicked",Toast.LENGTH_SHORT).show()
        specifyTheRightAndWrongAnswer(0)
    }
    fun button2isClicked(buttonView: View){
//        Toast.makeText(
//            this,"Button 2 is clicked",Toast.LENGTH_SHORT).show()
        specifyTheRightAndWrongAnswer(1)
    }
    fun button3isClicked(buttonView: View){
//        Toast.makeText(
//            this,"Button 3 is clicked",Toast.LENGTH_SHORT).show()
        specifyTheRightAndWrongAnswer(2)
    }
    fun button4isClicked(buttonView: View){
//        Toast.makeText(
//            this,"Button 4 is clicked",Toast.LENGTH_SHORT).show()
        specifyTheRightAndWrongAnswer(3)
    }

    //--------------------------Image Clicked----------------------------//

    fun imageViewIsClicked(view: View){

        var randomNumber: Int=(Math.random() * 6).toInt()+1
        Log.i("TAG","THE RANDOM NUMBER IS: $randomNumber")

        when (randomNumber){
            1-> btnOpenCamera.setBackgroundColor(Color.YELLOW)
            2-> btnOpenPhotoGallery.setBackgroundColor(Color.MAGENTA)
            3-> button1.setBackgroundColor(Color.RED)
            4-> button2.setBackgroundColor(Color.GREEN)
            5-> button3.setBackgroundColor(Color.BLUE)
            6-> button4.setBackgroundColor(Color.BLACK)
        }

    }

    //--------------------------Download Plant JSON Data----------------------------//

    inner class DownloadingPlantTask: AsyncTask<String,Int,List<Plant>>(){
        override fun doInBackground(vararg params: String?): List<Plant>? {

            // can access background thread. Not user interface thread.

//            var downloadJSON: DownloadingObject = DownloadingObject()
//            var JSONData = downloadJSON.downloadJSONDataFromLink(
//                "https://plantplaces.com/perl/mobile/flashcard.pl")
//            Log.i("JSON",JSONData)
            val parsePlant = ParsePlantUtility()

            return parsePlant.parsePlantObjectFromJSONData()
        }
        override fun onPostExecute(result: List<Plant>?) {
            super.onPostExecute(result)
            // Can access user interface thread. Not background thread.
            var numberOfPlants = result?.size ?: 0

            if(numberOfPlants > 0){
                var randomPlantIndexForButton1: Int = (Math.random() * result!!.size).toInt()
                var randomPlantIndexForButton2: Int = (Math.random() * result!!.size).toInt()
                var randomPlantIndexForButton3: Int = (Math.random() * result!!.size).toInt()
                var randomPlantIndexForButton4: Int = (Math.random() * result!!.size).toInt()

                var allRandomPlants = ArrayList<Plant>()
                allRandomPlants.add(result.get(randomPlantIndexForButton1))
                allRandomPlants.add(result.get(randomPlantIndexForButton2))
                allRandomPlants.add(result.get(randomPlantIndexForButton3))
                allRandomPlants.add(result.get(randomPlantIndexForButton4))

                button1.text = result.get(randomPlantIndexForButton1).toString()
                button2.text = result.get(randomPlantIndexForButton1).toString()
                button3.text = result.get(randomPlantIndexForButton1).toString()
                button4.text = result.get(randomPlantIndexForButton1).toString()

                correctAnswerIndex = (Math.random()* allRandomPlants.size).toInt()
                correctPlant = allRandomPlants.get(correctAnswerIndex)

                val downloadingImageTask = DownloadingImageTask()
                downloadingImageTask.execute(allRandomPlants.get(correctAnswerIndex).pictureName)
            }
        }
    }

    //-----------------------Check For Internet Connection------------------------//

    private fun checkForInternetConnection(): Boolean{
        val connectivityManager = this.getSystemService(
            CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        val isDeviceConnectedToInternet = networkInfo != null && networkInfo.isConnectedOrConnecting
        if (isDeviceConnectedToInternet){
            return true
        }else {
            createAlert()
            return false
        }
    }
    private fun createAlert(){
        val alertDialog : AlertDialog =
            AlertDialog.Builder(this@MainActivity).create()
        alertDialog.setTitle("Network Error")
        alertDialog.setMessage("Please check for internet connection")

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE,"OK",{
            dialog: DialogInterface?, which: Int ->

            startActivity(Intent(Settings.ACTION_SETTINGS))
        })

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL", {
            dialog: DialogInterface?, which: Int ->

            Toast.makeText(this@MainActivity,
            "You must be connected to the internet",
            Toast.LENGTH_SHORT).show()
            finish()
        })
        alertDialog.show()
    }

    //--------------------------Specify Wrong / Right-----------------------------//

    private fun specifyTheRightAndWrongAnswer(userGuess: Int){
        when (correctAnswerIndex){
            0->button1.setBackgroundColor(Color.GREEN)
            1->button2.setBackgroundColor(Color.GREEN)
            2->button3.setBackgroundColor(Color.GREEN)
            3->button4.setBackgroundColor(Color.GREEN)
        }
        if (userGuess == correctAnswerIndex){
            textView.setText("Right!")
            numberOfTimesUserAnsweredCorrectly++
            tvRightAnswers.setText("$numberOfTimesUserAnsweredCorrectly")
        }else{
            numberOfTimesUserAnsweredIncorrectly++
            tvWrongAnswers.setText("${numberOfTimesUserAnsweredIncorrectly}Correctly")
            var correctPlantName = correctPlant.toString()
            textView.setText("Wrong. Choose : $correctPlantName")
        }
    }

    //-------------------------Downloading Image task---------------------------//

    inner class DownloadingImageTask: AsyncTask<String, Int, Bitmap?>(){
        override fun doInBackground(vararg pictureName: String?): Bitmap? {
            try{
                val downloadingObject = DownloadingObject()
                val plantBitmap:Bitmap? = downloadingObject.downloadPicture(pictureName[0])
                return plantBitmap
            }catch(e:Exception){
                e.printStackTrace()
            }
            return null
        }

        override fun onPostExecute(result: Bitmap?) {
            super.onPostExecute(result)
            setProgressBar(false)
            displayUIWidgets(true)
            var listOfViews: Array<Any> = arrayOf(imageTaken)
            playAnimationOnView(Techniques.Tada,
                imageTaken,
                button1,
                button2,
                button3,
                button4,
                textView,
                tvWrongAnswers,
                tvRightAnswers
            )
            imageTaken.setImageBitmap(result)
        }
    }

    //-------------------------Progress Bar Visibility---------------------------//

    private fun setProgressBar (show: Boolean){

        if(show){
            grayScreen.setVisibility(View.VISIBLE)
            progressBar.setVisibility(View.VISIBLE)
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
        } else {
            grayScreen.setVisibility(View.GONE)
            progressBar.setVisibility(View.GONE)
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
    }

    //-------------------------Set Visibility of UI---------------------------//

    private fun displayUIWidgets(display: Boolean){
        var visibility = if (display) View.VISIBLE else View.INVISIBLE
        imageTaken.setVisibility(visibility)
        button1.setVisibility(visibility)
        button2.setVisibility(visibility)
        button3.setVisibility(visibility)
        button4.setVisibility(visibility)
        textView.setVisibility(visibility)
        tvRightAnswers.setVisibility(visibility)
        tvWrongAnswers.setVisibility(visibility)
    }
    //-------------------------Set Visibility of UI---------------------------//

    private fun playAnimationOnView(technique: Techniques, vararg views: View){
        for (view in views){
            YoYo.with(technique)
                .duration(700)
                .repeat(0)
                .playOn(view);
        }

    }
}