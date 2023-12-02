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
        val index = arguments?.getInt("index")

        val bitmap = BitmapFactory.decodeFile(imagePath)
        imageView.setImageBitmap(bitmap)

        view.setOnClickListener {
            dismiss()
        }

        val deleteButton = view.findViewById<Button>(R.id.deleteButton)
        deleteButton.setOnClickListener {
            if (context is CreateReviewActivity) {
                val createActivity = context as CreateReviewActivity
                createActivity.replaceReviewImages(Review.removeImagePathAtIndex(imagePath!!, index!!))
                createActivity.displayImages()
            } else if (context is EditReviewActivity) {
                val editActivity = context as EditReviewActivity
                editActivity.replaceReviewImages(Review.removeImagePathAtIndex(imagePath!!, index!!))
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