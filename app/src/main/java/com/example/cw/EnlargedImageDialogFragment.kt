package com.example.cw

import android.app.Dialog
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment

class EnlargedImageDialogFragment : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_enlarged_image, container, true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val context = requireContext()
        val imageView = view.findViewById<ImageView>(R.id.enlargedImage)
        val imagePath = arguments?.getString("imagePath")
        val path = arguments?.getString("path")

        val bitmap = BitmapFactory.decodeFile(path)
        imageView.setImageBitmap(bitmap)

        view.setOnClickListener {
            dismiss()
        }

        val deleteButton = view.findViewById<Button>(R.id.deleteButton)
        if (context !is CreateReviewActivity) {
            deleteButton.isVisible = false
        }
        deleteButton.setOnClickListener {
            if (context is CreateReviewActivity) {
                val createActivity = context as CreateReviewActivity
                createActivity.replaceReviewImages(Review.removeImagePath(imagePath!!, path!!))
                createActivity.displayImages()
            }
            dismiss()

        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }
}