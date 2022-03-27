# EncryptMP3File
此為加密Mp3練習的App(利用AES對稱式加密),使用者輸入IV_AES及AES_KEY並選擇檔案

程式筆記:

獲取手機上的audio檔選擇回傳uri:

    private fun pick_Audio(){
        val uri=android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val audio_picker_intent=Intent(Intent.ACTION_PICK,uri)
        startActivityForResult(audio_picker_intent,READ_PERMISSION_CODE)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==READ_PERMISSION_CODE && resultCode== Activity.RESULT_OK){
            val uri=data!!.data
        }
    }

加密MP3前轉byteArray作法:

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
      
AES加密ByteArray:

    private fun EncryptMP3ByAES(ivAes:ByteArray,Aeskey:ByteArray,Mp3ByteArray:ByteArray):ByteArray{
        try{
            val myAlgorithmParameterSpec:AlgorithmParameterSpec=IvParameterSpec(ivAes)
            val mySecretKeySpec=SecretKeySpec(Aeskey,"AES")
            val myCipher:Cipher= Cipher.getInstance("AES/CBC/PKCS5Padding")
            myCipher.init(Cipher.ENCRYPT_MODE,mySecretKeySpec,myAlgorithmParameterSpec)
            return myCipher.doFinal(Mp3ByteArray)
        }catch (e:Exception){
            e.printStackTrace()
        }
        return  ByteArray(0)
    }
        
AES解密ByteArray:

    private fun DecryptByteArrayToMp3(ivAes: ByteArray,Aeskey: ByteArray,Mp3ByteArray_Encrypt: ByteArray):ByteArray{
        try {
            val myAlgorithmParameterSpec:AlgorithmParameterSpec=IvParameterSpec(ivAes)
            val mySecretKeySpec=SecretKeySpec(Aeskey,"AES")
            val myCipher:Cipher= Cipher.getInstance("AES/CBC/PKCS5Padding")
            myCipher.init(Cipher.DECRYPT_MODE,mySecretKeySpec,myAlgorithmParameterSpec)
            return myCipher.doFinal(Mp3ByteArray_Encrypt)
        }
        catch (e:Exception){
            e.printStackTrace()
        }
        return ByteArray(0)
    }
    
 儲存加密資料:
 
 
     private fun getExtermalStoragePublicDir(FolderName:String):File{
        val file=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)//獲取公開資料夾
        if(!file.exists()){

            file.mkdir()//製造路徑
        }
        else{
            Log.e("JAMES","inLoopFile")
            val f=File(file,FolderName)//當前資料夾及其子目錄
            if(!f.exists()){
                Log.e("JAMES","inLoop_f")
                f.mkdir()
                return f
            }
            else{
                return  File(file,FolderName)
            }
        }
        return  File(file,FolderName)
    } 
     private fun Store_EncryptMP3(EncryptedByteArray: ByteArray,filename:String){
            val path=getExtermalStoragePublicDir("EncryptMP3").path
            val f=File(path,filename)
            try{
                //輸出資料
                val outStream=FileOutputStream(f)
                outStream.write(EncryptedByteArray)
                outStream.close()
            }catch (e:Exception){
                e.printStackTrace()
            }

        }
  儲存KEY在JPG檔:
  
  原因:
  
  在尋找其檔案發現非.jpg .mp3 .mp4 找不到其路徑(包括.txt檔找不到)下方寫入KEY至JPG的方式:
  
    val KeyStorePath=getExtermalStoragePublicDir("EncryptMP3_Key_Text").path
                val f1=File(KeyStorePath,"IVAES.jpg")
                val f2=File(KeyStorePath,"KEY.jpg")
                try{
                    val outStream_IVAES=FileOutputStream(f1)
                    outStream_IVAES.write(IVAES.toByteArray())
                    outStream_IVAES.close()
                    val outputStream_KEY=FileOutputStream(f2)
                    outputStream_KEY.write(AESKEY.toByteArray())
                    outputStream_KEY.close()
                }catch (e:Exception){
                    e.printStackTrace()
                }
  
