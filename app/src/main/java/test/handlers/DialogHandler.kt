package test.handlers

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import com.funapp.wallpaperautochangeexample.R
import test.Prefs
import test.RATE
import test.functions.putAny

object DialogHandler {

    private lateinit var alertDialog: AlertDialog

    /**
     *  simple ok dialog
     */

    fun simpleOk(context: Context , title : String , message:String , positive: DialogInterface.OnClickListener){
        val builder = AlertDialog.Builder(context , R.style.MyDialogTheme)
        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton("Ok" , positive)
            .setNegativeButton("Cancel"){dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
        alertDialog = builder.create()
        alertDialog.show()
    }

    fun rateUs(context: Context , callback: () -> Unit){
        val builder = AlertDialog.Builder(context , R.style.MyDialogTheme)

        builder.setPositiveButton("Rate Now"){ dialog, _ ->
            callback()
        }

        builder.setNegativeButton("Later"){ dialog, _ ->
            dialog.dismiss()
        }

        builder.setNeutralButton("Never"){ dialog, _ ->
            Prefs.putAny(RATE, false)
        }

        alertDialog = builder.create()
        alertDialog.setView(LayoutInflater.from(context).inflate(R.layout.inflator_rate , null))
        alertDialog.show()
    }

    fun dismiss(){
        alertDialog.dismiss()
    }
}