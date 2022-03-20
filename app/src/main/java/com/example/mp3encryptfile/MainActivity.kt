package com.example.mp3encryptfile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.ByteArrayOutputStream
import java.io.FileInputStream

class MainActivity : AppCompatActivity() {
    private lateinit var btn_chooseFile:Button
    private lateinit var editText_KeyNuber:EditText
    private lateinit var tv_FileName:TextView
    private lateinit var btn_encryptFile:Button
    private lateinit var btn_keySetting:Button
    private var AESKEY=""
    private var AudioPath=""
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
        editText_KeyNuber=findViewById(R.id.editTextTextKeyNumber)
        tv_FileName=findViewById(R.id.textView_fileName)
        btn_encryptFile=findViewById(R.id.button_encrypt)
        btn_keySetting=findViewById(R.id.button_keySetting)
    }

    override fun onResume() {
        super.onResume()
        btn_keySetting.setOnClickListener {
            Log.e("JAMES","onClick_btn_KeySetting")
            if(editText_KeyNuber.text.length==16){
                AESKEY=editText_KeyNuber.text.toString()
                val imm:InputMethodManager=getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(this.currentFocus!!.windowToken,0)
                Toast.makeText(this,"KEY設定成功",Toast.LENGTH_SHORT).show()
            }
            else if(editText_KeyNuber.text.length<16){
                Toast.makeText(this,"KEY位元數不足16位元",Toast.LENGTH_SHORT).show()
                editText_KeyNuber.text=null
            }
            else if(editText_KeyNuber.text.length>16){
                Toast.makeText(this,"KEY位元數超過16位元",Toast.LENGTH_SHORT).show()
                editText_KeyNuber.text=null
            }
        }
        btn_chooseFile.setOnClickListener {
            Log.e("JAMES","Click_btn_chooseFile")
            pick_Audio()
        }
        btn_encryptFile.setOnClickListener {
            Log.e("JAMES","onClick_btn_encryptFile")
            Log.e("JAMES",AudioPath)
            Log.e("JAMES",AESKEY)
            if(AudioPath!="" && AESKEY!=""){
                Log.e("JAMES","AudioPathAndAesKeyIsNotNull")
                Log.e("JAMES",convertMP3ToByteArray(AudioPath).toString())
            }
        }
    }
    private fun convertMP3ToByteArray(Audio_path:String):ByteArray{
        val fis:FileInputStream=FileInputStream(Audio_path)
        val bos:ByteArrayOutputStream= ByteArrayOutputStream()
        val b:ByteArray = ByteArray(1024)
        var readNum=fis.read(b)
        while(readNum!=-1){
            bos.write(b,0,readNum)
        }
        val bytes:ByteArray=bos.toByteArray()
        return bytes
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