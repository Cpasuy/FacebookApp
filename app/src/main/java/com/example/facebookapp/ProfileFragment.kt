package com.example.facebookapp

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.example.facebookapp.databinding.FragmentProfileBinding
import com.google.gson.Gson
import java.io.File


class ProfileFragment(private val userActual:User) : Fragment() {
    private var _binding:FragmentProfileBinding?= null
    private val binding get() = _binding!!

    private val rotateOpen: Animation by lazy {AnimationUtils.loadAnimation( context,R.anim.rotate_open_anim)}
    private val rotateClose: Animation by lazy {AnimationUtils.loadAnimation( context,R.anim.rotate_close_anim)}
    private val fromButton: Animation by lazy {AnimationUtils.loadAnimation( context,R.anim.from_button_anim)}
    private val toButton: Animation by lazy {AnimationUtils.loadAnimation( context,R.anim.to_button_anim)}

    private var permissionAccepted = false
    var file: File? =null

    private var clicked = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater,container,false)
        val view = binding.root
        val cameralauncherProfile = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), :: cameraonResultProfile)
        val galerylauncherProfile = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), :: galeryonResultProfile)
        val cameralauncherCover = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), :: cameraonResultCover)
        val galerylauncherCover = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), :: galeryonResultCover)

        binding.editBtnProfile.setOnClickListener {
            onEditButtonClicked()
        }

        //binding.editTextTextPersonName.text = userActual.name

        binding.logOutBtn.setOnClickListener {
            val intent= Intent(context,LoginActivity::class.java)
            startActivity(intent)
        }
        binding.cargarImageProfile.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.setType("image/*");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            galerylauncherProfile.launch(intent)

        }
        binding.tomarImageProfile.setOnClickListener {
            requestPermissions(arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE),1)
            if(permissionAccepted) {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                file = File("${activity?.getExternalFilesDir(null)}/photo.png")
                val uri =
                    FileProvider.getUriForFile(requireContext(), context?.packageName!!, file!!)
                userActual.profileImage=uri.toString()

                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                cameralauncherProfile.launch(intent)
            }
        }
        binding.cargarImagePortada.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.setType("image/*");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            galerylauncherCover.launch(intent)
        }
        binding.tomarImagePortada.setOnClickListener {
            requestPermissions(arrayOf(Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE),1)
            if(permissionAccepted) {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                file = File("${activity?.getExternalFilesDir(null)}/photo.png")
                val uri =
                    FileProvider.getUriForFile(requireContext(), context?.packageName!!, file!!)

                userActual.coverImage=uri.toString()
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                cameralauncherCover.launch(intent)
            }
        }
        // Inflate the layout for this fragment
        return view
    }
    fun cameraonResultProfile(result: ActivityResult){
        if(result.resultCode == Activity.RESULT_OK){
            val bitmap = BitmapFactory.decodeFile(file?.path)
            val thumbnail = Bitmap.createScaledBitmap(bitmap, bitmap.width/4,bitmap.height/4,true)
            binding.imageProfile.setImageBitmap(thumbnail)
        }else if(result.resultCode == Activity.RESULT_CANCELED){
            file = null
        }
    }
    fun cameraonResultCover(result: ActivityResult){
        if(result.resultCode == Activity.RESULT_OK){
            val bitmap = BitmapFactory.decodeFile(file?.path)
            val thumbnail = Bitmap.createScaledBitmap(bitmap, bitmap.width/4,bitmap.height/4,true)
            binding.coverProfile.setImageBitmap(thumbnail)
        }else if(result.resultCode == Activity.RESULT_CANCELED){
            file = null
        }
    }
    fun galeryonResultProfile(result: ActivityResult){
        if(result.resultCode == Activity.RESULT_OK){
            val sourceTreeUri: Uri = result.data?.data!!
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                requireContext().contentResolver.takePersistableUriPermission(
                    sourceTreeUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
            }
            val uriImage = result.data?.data
            userActual.profileImage=uriImage.toString()
            uriImage?.let {
                binding.imageProfile.setImageURI(uriImage)
            }
        }else if(result.resultCode == Activity.RESULT_CANCELED){
            file = null
        }
    }
    fun galeryonResultCover(result: ActivityResult){
        if(result.resultCode == Activity.RESULT_OK){
            val sourceTreeUri: Uri = result.data?.data!!
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                requireContext().contentResolver.takePersistableUriPermission(
                    sourceTreeUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
            }
            val uriImage = result.data?.data
            userActual.coverImage =uriImage.toString()
            uriImage?.let {
                binding.coverProfile.setImageURI(uriImage)
            }
        }else if(result.resultCode == Activity.RESULT_CANCELED){
            file = null
        }
    }
    private fun onEditButtonClicked(){
        setVisibility(clicked)
        setAnimation(clicked)
        clicked = !clicked
    }

    private fun setAnimation(clicked:Boolean) {
        if(clicked){
            binding.cargarImageProfile.startAnimation(toButton)
            binding.tomarImageProfile.startAnimation(toButton)
            binding.cargarImagePortada.startAnimation(toButton)
            binding.tomarImagePortada.startAnimation(toButton)

        }else{
            binding.cargarImageProfile.startAnimation(fromButton)
            binding.tomarImageProfile.startAnimation(fromButton)
            binding.cargarImagePortada.startAnimation(fromButton)
            binding.tomarImagePortada.startAnimation(fromButton)

        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode==1){
            var allGrand = true
            for(result in grantResults){
                if(result == PackageManager.PERMISSION_DENIED){
                    allGrand = false
                    break
                }
            }
            permissionAccepted = allGrand
        }
    }
    private fun setVisibility(clicked:Boolean) {
        if(!clicked){
            binding.cargarImageProfile.visibility= View.VISIBLE
            binding.tomarImageProfile.visibility= View.VISIBLE
            binding.cargarImagePortada.visibility= View.VISIBLE
            binding.tomarImagePortada.visibility= View.VISIBLE
        }else{
            binding.cargarImageProfile.visibility= View.INVISIBLE
            binding.tomarImageProfile.visibility= View.INVISIBLE
            binding.cargarImagePortada.visibility= View.INVISIBLE
            binding.tomarImagePortada.visibility= View.INVISIBLE
        }
    }

    override fun onPause() {
        super.onPause()
        val json= Gson().toJson(userActual)
        val sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("currentState",json).apply()
    }
    override fun onResume() {
        super.onResume()

        val sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE)
        var json = sharedPreferences.getString("currentState","NO_DATA")
        if(json != "NO_DATA"){
            val currentState= Gson().fromJson(json.toString(),User::class.java)
            binding.coverProfile.setImageURI(Uri.parse(currentState.coverImage))
            binding.imageProfile.setImageURI(Uri.parse(currentState.profileImage))
            binding.editTextTextPersonName.setText(currentState.name)
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    companion object {

        @JvmStatic
        fun newInstance(user:User) = ProfileFragment(user).apply {
        }
    }
}