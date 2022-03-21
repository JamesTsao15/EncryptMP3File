package com.example.mp3encryptfile

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.IOException
import java.lang.Exception
import java.nio.file.Files
import java.security.spec.AlgorithmParameterSpec
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class MainActivity : AppCompatActivity() {
    private lateinit var btn_chooseFile:Button
    private lateinit var editText_IVAES:EditText
    private lateinit var editText_AESKey:EditText
    private lateinit var tv_FileName:TextView
    private lateinit var btn_encryptFile:Button
    private lateinit var btn_keySetting:Button
    private var IVAES=""
    private var AESKEY=""
    private var AudioPath=""
    private var IVAES_Is_Set_Sucessful=false
    private var AESKEY_Is_Set_Sucessful=false
    private val READ_PERMISSION_CODE=100
    private fun pick_Audio(){
        val uri=android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val audio_picker_intent=Intent(Intent.ACTION_PICK,uri)
        startActivityForResult(audio_picker_intent,READ_PERMISSION_CODE)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn_chooseFile=findViewById(R.id.button_chooseFile)
        editText_IVAES=findViewById(R.id.editTextTextIVAES)
        editText_AESKey=findViewById(R.id.editTextTextAES_Key)
        tv_FileName=findViewById(R.id.textView_fileName)
        btn_encryptFile=findViewById(R.id.button_encrypt)
        btn_keySetting=findViewById(R.id.button_keySetting)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
        btn_keySetting.setOnClickListener {
            Log.e("JAMES","onClick_btn_KeySetting")
            if(editText_IVAES.text.length==16){
                IVAES=editText_IVAES.text.toString()
                val imm:InputMethodManager=getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(this.currentFocus!!.windowToken,0)
                IVAES_Is_Set_Sucessful=true
            }
            else if(editText_IVAES.text.length<16){
                Toast.makeText(this,"IVAES位元數不足16位元",Toast.LENGTH_SHORT).show()
            }
            else if(editText_IVAES.text.length>16){
                Toast.makeText(this,"IVAES位元數超過16位元",Toast.LENGTH_SHORT).show()
            }
            if(editText_AESKey.text.length==32){
                AESKEY=editText_AESKey.text.toString()
                val imm:InputMethodManager=getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(this.currentFocus!!.windowToken,0)
                AESKEY_Is_Set_Sucessful=true
            }
            else if(editText_AESKey.text.length<32){
                Toast.makeText(this,"KEY位元數不足32位元",Toast.LENGTH_SHORT).show()
            }
            else if(editText_IVAES.text.length>32){
                Toast.makeText(this,"KEY位元數超過32位元",Toast.LENGTH_SHORT).show()
            }
            if(IVAES_Is_Set_Sucessful && AESKEY_Is_Set_Sucessful){
                Toast.makeText(this,"Key設定成功",Toast.LENGTH_SHORT).show()
                IVAES_Is_Set_Sucessful=false
                AESKEY_Is_Set_Sucessful=false
            }
        }
        btn_chooseFile.setOnClickListener {
            Log.e("JAMES","Click_btn_chooseFile")
            pick_Audio()
        }
        btn_encryptFile.setOnClickListener {
            Log.e("JAMES","onClick_btn_encryptFile")
            Log.e("JAMES",AudioPath)
            if(AudioPath!=""){
                Log.e("JAMES","AudioPathAndAesKeyIsNotNull")
                val byteArray_MP3=convertMP3ToByteArray(AudioPath)
                Log.e("JAMES",Arrays.toString(byteArray_MP3))
                val byteArray_MP3_Encrypt=EncryptMP3ByAES(IVAES.toByteArray(),AESKEY.toByteArray(),byteArray_MP3)
                Log.e("JAMES",Arrays.toString(byteArray_MP3_Encrypt))
                Log.e("JAMES",Base64.getEncoder().encodeToString(byteArray_MP3_Encrypt))
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun convertMP3ToByteArray(Audio_path:String):ByteArray{
        try {
            val file =File(Audio_path)
            val bytes= Files.readAllBytes(file.toPath())
            return bytes
        }catch (e:IOException){
            e.printStackTrace()
        }
        return ByteArray(0)
    }
    private fun EncryptMP3ByAES(ivAes:ByteArray,Aeskey:ByteArray,Mp3ByteArray:ByteArray):ByteArray{
        try{
            val myAlgorithmParameterSpec:AlgorithmParameterSpec=IvParameterSpec(ivAes)
            val mySecretKeySpec=SecretKeySpec(Aeskey,"AES")
            var myCipher:Cipher= Cipher.getInstance("AES/CBC/PKCS5Padding")
            myCipher.init(Cipher.ENCRYPT_MODE,mySecretKeySpec,myAlgorithmParameterSpec)
            return myCipher.doFinal(Mp3ByteArray)
        }catch (e:Exception){
            e.printStackTrace()
        }
        return  ByteArray(0)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==READ_PERMISSION_CODE && resultCode== Activity.RESULT_OK){
            val uri=data!!.data
            val uriPathHelper=URIPathHelper()
            val audioPath= uri?.let { uriPathHelper.getPath(this, it) }
            AudioPath=audioPath.toString()
            tv_FileName.text="選擇音檔:$AudioPath"
        }
    }
}