package com.ignitedminds.classroomhelper.Utils

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import com.ignitedminds.classroomhelper.R

class GenericOnKeyListener(var view: View) : View.OnKeyListener {
    companion object {
        var array: MutableList<EditText> = mutableListOf()
        var curr : Int = 0
        var callback : GenericOnKeyListenerOnBackPressed? =null
        fun setUpWatcher(callback : GenericOnKeyListenerOnBackPressed){
            this.callback=callback
            curr=0
            array.clear()
        }
    }

    init {
        val editText = view as EditText
        array.add(editText)

    }

    override fun onKey(p0: View?, p1: Int, p2: KeyEvent?): Boolean {
        if(p2==null)
            return true
        if (p2.action!=KeyEvent.ACTION_DOWN)
            return true
        if(p1 in 7..16){
            if(curr<=5){
                if(array[curr].text.isEmpty()){
                    array[curr].setText("${p1-7}")
                    if(curr!=5) {curr++
                        array[curr].requestFocus()
                    }
                }
            }
        }else if(p1==KeyEvent.KEYCODE_DEL){
            if(array[curr].text.isEmpty()){
                if(curr>0){
                    curr--
                    array[curr].setText("")
                    array[curr].requestFocus()
                }
            }else{
                array[curr].setText("")
            }
        }else if(p1==KeyEvent.KEYCODE_BACK){
            callback?.onBackKeyPressed()
        }
        return true
    }
}