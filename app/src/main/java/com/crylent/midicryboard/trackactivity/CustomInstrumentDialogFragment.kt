package com.crylent.midicryboard.trackactivity

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import com.crylent.midicryboard.Instruments
import com.crylent.midicryboard.R
import com.crylent.midicryboard.projectactivities.Files
import com.crylent.midicryboard.toJson
import com.crylent.midilib.instrument.AssetInstrument
import com.crylent.midilib.instrument.SynthInstrument
import java.io.FileOutputStream

enum class CustomInstrumentType {
    NOT_SELECTED, SYNTH, FROM_ASSET
}

class CustomInstrumentDialogFragment(
    private val categoryId: Int,
    private val onCreatedCallback: () -> Unit
): DialogFragment() {

    private var typeSelected = CustomInstrumentType.NOT_SELECTED
    private var assetSelected: Uri? = null
    private var nameTyped = ""

    private lateinit var confirmButton: Button
    private lateinit var assetSelectionArea: View
    private lateinit var selectAssetButton: Button
    private lateinit var baseNoteSpinner: Spinner

    private fun updateButtonState() {
        confirmButton.isEnabled = nameTyped.isNotEmpty() &&
                (typeSelected == CustomInstrumentType.SYNTH
                        || typeSelected == CustomInstrumentType.FROM_ASSET
                        && assetSelected != null)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
                .setTitle("Custom Instrument")
                .setView(R.layout.alertdialog_create_custom)
                .setPositiveButton("Create") { _, _ ->
                    val instrument = (if (typeSelected == CustomInstrumentType.FROM_ASSET) {
                        createAssetInstrument()
                    } else SynthInstrument()).apply {
                        name = nameTyped
                    }
                    val category = Instruments.getCategoryById(categoryId)
                    category.items.add(instrument)
                    requireContext().also { context ->
                        FileOutputStream(
                            Files.preset(context, category.name, instrument.name)
                        ).use { stream ->
                            stream.bufferedWriter().use { writer ->
                                writer.write(
                                    instrument.toJson(context).toString()
                                )
                            }
                        }

                    }
                    onCreatedCallback()
                }
                .setNegativeButton("Cancel") { _, _ -> }
            builder.create().apply {
                setOnShowListener { onShow() }
            }
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun onShow() {
        (dialog as AlertDialog).apply {
            confirmButton = getButton(AlertDialog.BUTTON_POSITIVE).apply {
                isEnabled = false
            }

            // Synth/asset instrument radio switch
            findViewById<RadioGroup>(R.id.createCustomType)!!.apply {
                setOnCheckedChangeListener { _, checkedId ->
                    typeSelected = when (checkedId) {
                        R.id.createSynthCustom -> CustomInstrumentType.SYNTH
                        R.id.createAssetCustom -> CustomInstrumentType.FROM_ASSET
                        else -> CustomInstrumentType.NOT_SELECTED
                    }
                    assetSelectionArea.visibility =
                        if (typeSelected == CustomInstrumentType.FROM_ASSET) VISIBLE
                        else INVISIBLE
                    updateButtonState()
                }
            }

            // Asset selection area
            assetSelectionArea = findViewById(R.id.assetSelectionArea)!!
            selectAssetButton = findViewById<Button>(R.id.selectAsset)!!.apply {
                setOnClickListener {
                    selectAsset()
                }
            }
            baseNoteSpinner = findViewById<Spinner>(R.id.baseNoteSpinner)!!.apply {
                adapter = ArrayAdapter.createFromResource(
                    context, R.array.notes, android.R.layout.simple_spinner_item
                ).apply {
                    setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                }
                setSelection(38) // D3
            }

            // Instrument name field
            findViewById<EditText>(R.id.customInstrumentName)!!.apply {
                addTextChangedListener {
                    nameTyped = it.toString()
                    updateButtonState()
                }
            }
        }
    }

    private fun createAssetInstrument() = AssetInstrument().apply {
        requireContext().apply {
            contentResolver.openInputStream(assetSelected!!)!!.use {
                loadAsset(
                    this,
                    baseNoteSpinner.selectedItemPosition.toByte(),
                    it.readBytes(),
                    true
                )
            }
        }
    }

    private val selectAssetLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode != Activity.RESULT_OK) return@registerForActivityResult

        assetSelected = it.data?.data
        val filename = getAssetName(assetSelected!!)
        selectAssetButton.text = String.format(resources.getString(R.string.asset_selected), filename)
        Log.i("CustomInstrument", "Selected asset: $filename (${assetSelected.toString()})")
        updateButtonState()
    }

    private fun getAssetName(asset: Uri): String {
        val cursor = requireContext().contentResolver.query(asset, null, null, null, null)!!
        val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        cursor.moveToFirst()
        return cursor.getString(index).also {
            cursor.close()
        }
    }

    private fun selectAsset() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "audio/wav"
            putExtra(Intent.EXTRA_MIME_TYPES, "audio/x-wav") // can be wav or x-wav
        }
        selectAssetLauncher.launch(intent)
    }

    companion object {
        const val TAG = "CustomInstrumentDialog"
    }
}