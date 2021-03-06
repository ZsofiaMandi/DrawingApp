package com.example.kidsdrawingapp

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View

class DrawingView(context: Context, attrs: AttributeSet) : View(context, attrs){

    private var mDrawPath : CustomPath? =
        null // A variable of CustomPath inner class to use it further
    private var mCanvasBitmap: Bitmap? = null // An instance of the Bitmap
    private var mDrawPaint: Paint? =
        null // The Paint class holds the style and color information about how to draw?
    private var mCanvasPaint: Paint? = null // Instance of canvas paint view
    private var mBrushSize: Float =
        0.toFloat() // A variable for stroke/brush size to draw on the canvas
    private var color = Color.BLACK // A variable to hold de stroke/ brush size to draw on the canvas
    private var canvas: Canvas? = null
    private val mPaths = ArrayList<CustomPath>()
    private val mUndoPaths = ArrayList<CustomPath>()

    init{
        setupDrawing()
    }

    // Removing the last path from the saved paths
    fun onClickUndo(){
        if(mPaths.size > 0){
            // removing the last paths from the mPaths ArrayList
                // and adding it to the UndoPaths ArrayList
            mUndoPaths.add(mPaths.removeAt(mPaths.size - 1))
            invalidate() // This will call the onDraw override method
        }
    }

    // Pasting back the last removed path to the saved path
    fun onClickRedo(){
        if(mUndoPaths.size > 0){
            mPaths.add(mUndoPaths.removeAt(mUndoPaths.size - 1))
            invalidate()
        }
    }

    // Setup the prepared variables
    private fun setupDrawing(){
        mDrawPaint = Paint()
        mDrawPath = CustomPath(color, mBrushSize)
        mDrawPaint!!.color = color
        mDrawPaint!!.style = Paint.Style.STROKE
        mDrawPaint!!.strokeJoin = Paint.Join.ROUND
        mDrawPaint!!.strokeCap = Paint.Cap.ROUND
        mCanvasPaint = Paint(Paint.DITHER_FLAG)
        //mBrushSize = 20.toFloat()

    }

    // Setting the canvas size to be correct
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mCanvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        canvas = Canvas(mCanvasBitmap!!)
    }

    // Drawing on Canvas
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(mCanvasBitmap!!, 0f, 0f, mCanvasPaint)

        // Drawing all of the saved paths
        for(path in mPaths){
            mDrawPaint!!.strokeWidth = path.brushThickness
            mDrawPaint!!.color = path.color
            canvas.drawPath(path, mDrawPaint!!)
        }
        
        // Drawing a path
        if(!mDrawPath!!.isEmpty){
            mDrawPaint!!.strokeWidth = mDrawPath!!.brushThickness
            mDrawPaint!!.color = mDrawPath!!.color
            canvas.drawPath(mDrawPath!!, mDrawPaint!!)
        }
    }

    // Draw on touch -> Give mDrawPath a value, so it wont be empty
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val touchX = event?.x
        val touchY = event?.y

        // Motion events we have: finger down on the screen, move and up
        when(event?.action){
            MotionEvent.ACTION_DOWN -> {
                mDrawPath!!.color = color
                mDrawPath!!.brushThickness = mBrushSize

                mDrawPath!!.reset()
                if (touchX != null && touchY != null) {
                    mDrawPath!!.moveTo(touchX, touchY)
                }
            }

            MotionEvent.ACTION_MOVE ->{
                if (touchX != null && touchY != null) {
                    mDrawPath!!.lineTo(touchX, touchY)
                }
            }

            MotionEvent.ACTION_UP ->{
                // Saving the path in the mPaths array list
                // to have multiple paths on the screen at the same time
                mPaths.add(mDrawPath!!)
                mUndoPaths.removeAll(mUndoPaths)
                mDrawPath = CustomPath(color, mBrushSize)
            }
            else -> return false
        }

        // invalidate the whole view if the view is visible
        invalidate()

        return true
    }

    fun setSizeForBrush(newSize: Float){
        // Taking the screen size into consideration
        mBrushSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            newSize, resources.displayMetrics)
        mDrawPaint!!.strokeWidth = mBrushSize
    }

    // Setting the size for the brush
    fun setSizeForBrush(newSize: Float){
        // Taking the screen size into consideration
        mBrushSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            newSize, resources.displayMetrics)
        mDrawPaint!!.strokeWidth = mBrushSize
    }


    // Setting the selected color
    fun setColor(newColor: String){
        color = Color.parseColor(newColor)
        mDrawPaint!!.color = color
    }

    // A function to set the color from the custom ColorPicker dialog
    fun setCustomColor(newColor: Int){
        color = newColor
        mDrawPaint!!.color = color
    }

    // Inner class for customPath
    internal inner class CustomPath(var color: Int,
                                    var brushThickness: Float) : Path() {}

}