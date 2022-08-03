package com.mhy.landrestoration.util

import android.content.Context
import android.content.DialogInterface
import android.view.View
import android.widget.SimpleAdapter
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mhy.landrestoration.R

class ShowAlert {

    private var icon = R.drawable.ic_baseline_warning_24_red

    fun show(context: Context, title: String, msg: String) {
        show(context, null, title, msg, "確定", null, null, null)
    }

    fun show(context: Context, title: String, msg: String, icon: Int) {
        show(context, null, title, msg, "確定", null, null, null, icon)
    }

    fun show(
        context: Context, title: String, msg: String, positiveTitle: String?,
        onPositiveClick: DialogInterface.OnClickListener?, icon: Int = this.icon
    ) {
        show(context, null, title, msg, positiveTitle, onPositiveClick, null, null, icon)
    }

    fun show(
        context: Context, view: View, title: String, msg: String?, positiveTitle: String?,
        onPositiveClick: DialogInterface.OnClickListener?, icon: Int = this.icon
    ) {
        show(context, view, title, msg, positiveTitle, onPositiveClick, null, null, icon)
    }


    fun show(
        context: Context,
        title: String,
        msg: String,
        positiveTitle: String?,
        onPositiveClick: DialogInterface.OnClickListener?,
        neutralTitle: String?,
        onNeutralClick: DialogInterface.OnClickListener?,
        icon: Int = this.icon,
        cancelable: Boolean = false
    ) {
        show(
            context,
            null,
            title,
            msg,
            positiveTitle,
            onPositiveClick,
            neutralTitle,
            onNeutralClick,
            icon,
            cancelable
        )
    }

    fun show(
        context: Context,
        view: View?,
        title: String,
        msg: String?,
        positiveTitle: String?,
        onPositiveClick: DialogInterface.OnClickListener?,
        neutralTitle: String?,
        onNeutralClick: DialogInterface.OnClickListener?,
        icon: Int = this.icon,
        cancelable: Boolean = false
    ) {
        val builder = MaterialAlertDialogBuilder(
            context,
            R.style.MyThemeOverlay_MaterialComponents_MaterialAlertDialog
        )
            .setTitle(title)
            .setIcon(icon)
            .setCancelable(cancelable) //禁止按任意處關閉
            .setMessage(msg)
            .setPositiveButton(positiveTitle, onPositiveClick)
            .setNeutralButton(neutralTitle, onNeutralClick)
        if (view != null) {
            builder.setView(view)
        }
        builder.show()
    }

    fun show(
        context: Context,
        view: View?,
        title: String,
        msg: String?,
        positiveTitle: String?,
        onPositiveClick: (AlertDialog, View) -> Unit,
        neutralTitle: String?,
        icon: Int = this.icon,
        cancelable: Boolean = false
    ) {
        val builder = MaterialAlertDialogBuilder(
            context,
            R.style.MyThemeOverlay_MaterialComponents_MaterialAlertDialog
        )
            .setTitle(title)
            .setIcon(icon)
            .setCancelable(cancelable) //禁止按任意處關閉
            .setMessage(msg)
            .setPositiveButton(positiveTitle, null)
            .setNeutralButton(neutralTitle, null)
        if (view != null) {
            builder.setView(view)
        }
        val dialog = builder.show()
        val positiveBtn = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        positiveBtn.setOnClickListener {
            onPositiveClick(dialog, it)
        }
    }

    fun show(
        context: Context,
        title: String,
        simpleAdapter: SimpleAdapter,
        positiveTitle: String?,
        onPositiveClick: DialogInterface.OnClickListener?,
        neutralTitle: String?,
        onNeutralClick: DialogInterface.OnClickListener?,
        icon: Int = this.icon,
        cancelable: Boolean = false
    ) {
        val builder = MaterialAlertDialogBuilder(
            context,
            R.style.MyThemeOverlay_MaterialComponents_MaterialAlertDialog
        )
            .setTitle(title)
            .setAdapter(simpleAdapter, null)
            .setIcon(icon)
            .setCancelable(cancelable) //禁止按任意處關閉
            .setPositiveButton(positiveTitle, onPositiveClick)
            .setNeutralButton(neutralTitle, onNeutralClick)
        builder.show()
    }
}