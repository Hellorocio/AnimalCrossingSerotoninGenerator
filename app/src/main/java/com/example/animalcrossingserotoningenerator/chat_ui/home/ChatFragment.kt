package com.example.animalcrossingserotoningenerator

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.animalcrossingserotoningenerator.ChatActivity
import com.example.animalcrossingserotoningenerator.ChatViewModel
import com.example.animalcrossingserotoningenerator.R
import edu.cs371m.firestore.ChatRow
import kotlinx.android.synthetic.main.fragment_chat.view.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.UUID.randomUUID

class ChatFragment : Fragment() {

    private lateinit var viewModel: ChatViewModel
    private lateinit var myHomeRV: RecyclerView
    private lateinit var messageET: EditText
    private lateinit var chatAdapter: FirestoreChatAdapter
    // Is there an outstanding photo, if so where?
    private var currentPhotoPath: String? = null

    private fun initRecyclerView(root: View)  {
        myHomeRV = root.findViewById(R.id.homeRV)
        chatAdapter = FirestoreChatAdapter(viewModel)
        myHomeRV.adapter = chatAdapter
        myHomeRV.layoutManager = LinearLayoutManager(context)
        val itemDecor = DividerItemDecoration(myHomeRV.context, LinearLayoutManager.VERTICAL)
        itemDecor.setDrawable(ContextCompat.getDrawable(myHomeRV.context, (R.drawable.divider))!!)
        myHomeRV.addItemDecoration(itemDecor)
    }
    private fun clearCompose() {
        messageET.text.clear()
        messageET.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
        currentPhotoPath = null
    }
    private fun initMessageCompose(root: View) {
        // Take a picture button
        root.findViewById<ImageButton>(R.id.composeCameraIB).setOnClickListener {
            Log.d(ChatActivity.TAG, "Camera intent")
            takePictureIntent()
        }
        // Message text area
        messageET = root.findViewById(R.id.composeMessageET)
        // Send message button
        root.findViewById<ImageButton>(R.id.composeSendIB).setOnClickListener {
            if( messageET.text.isNotEmpty()) {
                val chatRow = ChatRow().apply {
                    name = viewModel.getDisplayName()
                    ownerUid = viewModel.getUid()
                    message = messageET.text.toString()
                    // XXX Write me


                    if(currentPhotoPath!=null) {
                        pictureUUID = randomUUID().toString()
                        viewModel.uploadJpg(currentPhotoPath!!, pictureUUID.toString())
                    }

                    clearCompose()
                }
                viewModel.saveChatRow(chatRow)
            }
        }
    }
    //////////////////////////////////////////////////////////////////////
    // Camera stuff
    private val cameraRequestCode = 10
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir: File? = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            ?: File("")
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }
    private fun takePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Create the File where the photo should go
            val photoFile: File? = try {
                createImageFile()
            } catch (ex: IOException) {
                // Error occurred while creating the File
                Log.d(javaClass.simpleName, "Cannot create file", ex)
                null
            }
            // Continue only if the File was successfully created
            photoFile?.also {
                val photoURI: Uri = FileProvider.getUriForFile(
                    requireContext(),
                    "edu.cs371m.firestore",
                    it
                )
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, cameraRequestCode)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == cameraRequestCode && resultCode == AppCompatActivity.RESULT_OK) {
//             val bitmap = BitmapFactory.Options().run {
//                 inSampleSize = 4
//                // Decode bitmap with inSampleSize set
//                inJustDecodeBounds = false
//                BitmapFactory.decodeFile(currentPhotoPath, this)
//            }
            // Create a reasonable sized bitmap to display to the user
            val bitmap = BitmapFactory.decodeFile(currentPhotoPath)
            val bitmapResized = Bitmap.createScaledBitmap(bitmap, 500, 500, false)
            val drawable = BitmapDrawable(resources, bitmapResized)
            //val drawable = Drawable.createFromPath(currentPhotoPath)
            messageET.setCompoundDrawablesWithIntrinsicBounds(null, null, null, drawable)
        }
    }

    // Something might have changed.  Redo query
    override fun onResume() {
        super.onResume()
        viewModel.getChat()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = activity?.run {
            ViewModelProviders.of(this)[ChatViewModel::class.java]
        } ?: throw Exception("Invalid Activity")
        val root = inflater.inflate(R.layout.fragment_chat, container, false)
        root.findViewById<TextView>(R.id.chatTitleTV).text = "Let's talk!"

        initMessageCompose(root)

        initRecyclerView(root)
        viewModel.observeChat().observe(this, Observer {
            Log.d(ChatActivity.TAG, "Observe Chat $it")
            chatAdapter.submitList(it)
        })

        return root
    }
}