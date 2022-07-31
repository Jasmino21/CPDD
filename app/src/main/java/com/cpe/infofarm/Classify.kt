package com.cpe.infofarm

import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.cpe.infofarm.databinding.ActivityClassifyBinding
import com.cpe.infofarm.ml.PestModel8
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import org.tensorflow.lite.support.image.TensorImage
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class Classify : AppCompatActivity() {

    lateinit var binding: ActivityClassifyBinding
    lateinit var imageView: ImageView
    lateinit var english: ConstraintLayout
    lateinit var tagalog: ConstraintLayout
    private var imageUri: Uri? = null
    lateinit var button: Button
    lateinit var buttonLoad: Button
    lateinit var buttonTagalog: Button
    lateinit var buttonEnglish: Button
    lateinit var tvOutput: CollapsingToolbarLayout
    lateinit var display: TextView
    lateinit var displayTagalog: TextView
    lateinit var pestName: TextView
    lateinit var pestNameTagalog: TextView
    lateinit var pestControl: TextView
    lateinit var pesticide: TextView
    lateinit var pestControlTagalog: TextView
    lateinit var pesticideTagalog: TextView
    val GALLERY_REQUEST_CODE = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClassifyBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        imageView = binding.imageView
        button = binding.btnCaptureImage
        tvOutput = binding.collapsingToolbar
        display = binding.pestDescription
        displayTagalog = binding.pestDescriptionTagalog
        pestName = binding.pestName
        pestNameTagalog = binding.pestNameTagalog
        pestControl = binding.pestControl
        pesticide = binding.pestChemical
        pestControlTagalog = binding.pestControlTagalog
        pesticideTagalog = binding.pestChemicalTagalog
        buttonLoad = binding.btnLoadImage
        buttonTagalog = binding.btnTagalog
        buttonEnglish = binding.btnEnglish
        english = binding.english
        tagalog = binding.tagalog

        button.setOnClickListener{
            //check permission ng camera
            if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED)
            {
                takePicturePreview.launch(null)
            }else{
                requestPermission.launch(android.Manifest.permission.CAMERA)
            }
        }
            //button to load image
        buttonLoad.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
            ) {
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                intent.type = "image/*"
                val mimeTypes = arrayOf("image/jpeg", "image/png", "image/jpg")
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
                intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                onresult.launch(intent)
            } else {
                storeRequestPermission.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

        buttonTagalog.setOnClickListener{
            tagalog.visibility = ConstraintLayout.VISIBLE
            english.visibility = ConstraintLayout.INVISIBLE
        }

        buttonEnglish.setOnClickListener{
            tagalog.visibility = ConstraintLayout.INVISIBLE
            english.visibility = ConstraintLayout.VISIBLE
        }

        //redirect user to google search
        imageView.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=${pestName.text} in cabbage"))
            startActivity(intent)
        }

    }

    //request storage permission
    private val storeRequestPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()){ granted ->
        if(granted){
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            val mimeTypes = arrayOf("image/jpeg", "image/png", "image/jpg")
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            onresult.launch(intent)
        }else{
            Toast.makeText(this, "Permission Denied! Try Again", Toast.LENGTH_SHORT).show()
        }
    }

    //request camera permission
    private val requestPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()){ granted ->
        if(granted){
            takePicturePreview.launch(null)
        }else{
            Toast.makeText(this, "Permission Denied! Try Again", Toast.LENGTH_SHORT).show()
        }
    }
    //launch camera and take picture
    private val takePicturePreview = registerForActivityResult(ActivityResultContracts.TakePicturePreview()){ bitmap ->
        if(bitmap != null){
            imageView.setImageBitmap(bitmap)
            outputGenerator(bitmap)
        }
    }

    //to get image from gallery
    private val onresult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result->
        Log.i("TAG", "This is the result: ${result.data} ${result.resultCode}")
        onResultReceived(GALLERY_REQUEST_CODE,result)
    }

    private fun  onResultReceived(requestCode: Int, result: ActivityResult?){
        when(requestCode){
            GALLERY_REQUEST_CODE ->{
                if (result?.resultCode == Activity.RESULT_OK){
                    result.data?.data?.let{uri ->
                        Log.i("TAG", "onResultReceived: $uri")
                        imageUri = uri
                        val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(uri))
                        imageView.setImageBitmap(bitmap)
                        outputGenerator(bitmap)
                    }
                }else {
                    Log.e("TAG", "onActivityResult: error in selecting image")
                }
            }
        }
    }
   private fun isOnline(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                } else {
                    TODO("VERSION.SDK_INT < M")
                }
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        return false
    }
    private fun uploadImage() {
//        val progressDialog = ProgressDialog(this)
//        progressDialog.setMessage("Uploading File ...")
//        progressDialog.setCancelable(false)
//        progressDialog.show()

        val formatter1 = SimpleDateFormat("MM/yyyy", Locale.getDefault())
        val formatter2 = SimpleDateFormat("yyMMddHHmmssZ", Locale.getDefault())
        val now = Date()
        val dateReport = formatter1.format(now)
        val filename = formatter2.format(now)

        val storageReference = FirebaseStorage.getInstance().getReference("images/${pestName.text}-${filename}")

        imageUri?.let {
            storageReference.putFile(it).addOnSuccessListener{
                Toast.makeText(this, "Successfully Uploaded", Toast.LENGTH_SHORT).show()
    //            if (progressDialog.isShowing) progressDialog.dismiss()
                storageReference.downloadUrl.addOnCompleteListener(){taskSnapshot ->
                    val imageUrl = taskSnapshot.result.toString()
                    uploadFirestore(imageUrl, pestName.text.toString(), dateReport)
                }

            }.addOnFailureListener{
                Toast.makeText(this, "Failed to Upload", Toast.LENGTH_SHORT).show()
    //            if(progressDialog.isShowing) progressDialog.dismiss()
            }
        }
    }

    private fun uploadFirestore(imageUrl:String, pestName:String, dateReport: String) {
        val db = Firebase.firestore
        val data = hashMapOf(
            "imageUrl" to imageUrl,
            "pestName" to pestName,
            "date" to dateReport
        )
        db.collection("Report")
            .add(data)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot written with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }

    private fun outputGenerator(bitmap: Bitmap){
        //declaring tensor flow lite model variable

        val pestsModel = PestModel8.newInstance(this)

        // converting bitmap into tensor flow image
        val newBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val tfimage = TensorImage.fromBitmap(newBitmap)

        //process the image using trained model and sort it in descending order
        val outputs = pestsModel.process(tfimage)
            .probabilityAsCategoryList.apply {
                sortByDescending { it.score }
            }

        //getting result having high probability
        val highProbabilityOutput = outputs[0]

        Log.d("tag","FIRST: $highProbabilityOutput")
        //setting output text
        if (highProbabilityOutput.score >= 0.40){
            pestName.text = highProbabilityOutput.label
            pestNameTagalog.text = highProbabilityOutput.label
            getFireStore(pestName.text.toString())
            if(isOnline(this) && imageUri != null){
                uploadImage()
            }else{
                val formatter1 = SimpleDateFormat("MM/yyyy", Locale.getDefault())
                val now = Date()
                val dateReport = formatter1.format(now)
                uploadFirestore("none", pestName.text.toString(), dateReport)
                Toast.makeText(this, "Successfully Uploaded", Toast.LENGTH_SHORT).show()
            }
        }else{
            pestName.text =getString(R.string.no_info)
            display.text = getString(R.string.no_info)
            pestControl.text =  getString(R.string.no_info)
            pesticide.text = getString(R.string.no_info)

        }

//        saveFireStore(display.text.toString())
        Log.i("TAG", "Score: ${highProbabilityOutput.score}")
//        Log.i("TAG", "outputGenerator: ${tvOutput.text.toString()}")
    }

    private fun getFireStore(pest : String){
        val db = FirebaseFirestore.getInstance()

        db.collection("Pests")
            .document(pest)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.data !=null) {
                    Log.d("TAG", "DocumentSnapshot data: ${document.data?.get("control")}")
                    display.text = document.data?.get("description").toString()
                    pestControl.text = document.data?.get("control").toString().replace("\\n", "\n")
                    pesticide.text = document.data?.get("pesticide").toString().replace("\\n", "\n")
                    displayTagalog.text = document.data?.get("descriptionTagalog").toString()
                    pestControlTagalog.text = document.data?.get("controlTagalog").toString().replace("\\n", "\n")
                    pesticideTagalog.text = document.data?.get("pesticide").toString().replace("\\n", "\n")
                } else {
                    Log.d("TAG", "No such document")
                    display.text = ""
                }
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "get failed with ", exception)
            }
        Log.d("TAG", "No such document")
    }
    //fun that takes a bitmap and store to user's device
//    private fun downloadImage(mBitmap: Bitmap): Uri?{
//        val contentValues = ContentValues().apply {
//            put(MediaStore.Images.Media.DISPLAY_NAME,"pests_Images"+ System.currentTimeMillis()/1000)
//            put(MediaStore.Images.Media.MIME_TYPE,"image/png")
//        }
//        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
//        if (uri != null){
//            contentResolver.insert(uri, contentValues)?.also {
//                contentResolver.openOutputStream(it).use { outputStream ->
//                    if (!mBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)){
//                        throw IOException("Couldn't save the bitmap")
//                    }
//                    else{
//                        Toast.makeText(applicationContext, "Image Saved", Toast.LENGTH_SHORT).show()
//                    }
//                }
//                return it
//            }
//        }
//        return null
//    }
}