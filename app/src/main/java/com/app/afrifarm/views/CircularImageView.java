package com.app.afrifarm.views;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class CircularImageView extends androidx.appcompat.widget.AppCompatImageView {
	// Border & Selector configuration variables
	private boolean hasBorder;
	private boolean hasSelector;
	private boolean isSelected;
	private int borderWidth;
	private int canvasSize;
	private int selectorStrokeWidth;
	
	// Objects used for the actual drawing
	private BitmapShader shader;
	private Bitmap image;
	private Paint paint;
	private Paint paintBorder;
	private Paint paintSelectorBorder;
	private ColorFilter selectorFilter;
	private ScaleType mScaleType;
	private Matrix mDrawMatrix = null;
	private Matrix mMatrix;
	 private Drawable mDrawable = null;
	 private  int mDrawableWidth;
	    private  int mDrawableHeight;
	      int mPaddingLeft,mPaddingRight,mPaddingTop,mPaddingBottom;
	private  float radius =360;
	RectF rect = new RectF();
	private Drawable drawable;
	  float  maxY;
	  float maxX;
	  float cX;
	  float startY=-1;
	  float startX;
	  int startLeftmargin;
      int startTopmargin;
      private boolean hideDraw;
     private int position;
     	private boolean invalidateInBackground=false;
      	String uri;
      	FileType mFileType;
	private int oldCanvasSize;
	public CircularImageView(Context context)
	{
		this(context, null);
	}

	public CircularImageView(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
	}

	public CircularImageView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init(context, attrs, defStyle);
	}
	@Override
	public void setColorFilter(ColorFilter cf) {
		// TODO Auto-generated method stub
		super.setColorFilter(cf);
		if(getDrawable()!=null){
		getDrawable().setColorFilter(cf);
		invalidate();}
	}
	public void reset(){
		startY=-1;
		startX=-1;
		cX=0;
		
	}



	 public enum FileType{
		   Audio,
		   Video,
		   Image,
		   VideoNotSupported,
		   Text,
		   Webpage,Docs
		   ,Book,Pdf,ImgSelect,Colorselect
	   }
	public void setXY(float x){
		 RelativeLayout.LayoutParams params =(RelativeLayout.LayoutParams)this.getLayoutParams();
		cX=x;
		float smallX=(x-maxX)/maxX;
		float smallY=(float) Math.pow(smallX, 4);
		
		if(startY==-1){
			startY=getY();
			startX=getX();
			startTopmargin=params.topMargin;
			startLeftmargin=params.leftMargin;
		}
		params.leftMargin=(int) (x+startLeftmargin);
		
		params.topMargin=(int)(maxY*(1-smallY))+startTopmargin;
		this.setLayoutParams(params);
	//	this.setX(x+startX);
		float goingY=((maxY*(1-smallY))+startY);
	//this.setY(goingY);
	//	Log.i(getClass().getSimpleName(), "TOp margin "+params.topMargin+" vs "+maxY*(1-smallY));
	//	this.invalidate();
	}
	public float getXY(){
		return cX;
	}
	/**
	 * Initializes paint objects and sets desired attributes.
	 * 
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	private void init(Context context, AttributeSet attrs, int defStyle)
	{
		// Initialize paint objects
		paint = new Paint();
		paint.setAntiAlias(true);
		paintBorder = new Paint();
		paintBorder.setAntiAlias(true);
		paintSelectorBorder = new Paint();
		paintSelectorBorder.setAntiAlias(true);
		 mMatrix=new Matrix();
	}
	 private void configureBounds() {
	        if (mDrawable == null ) {
	            return;
	        }

	        int dwidth = mDrawableWidth;
	        int dheight = mDrawableHeight;

	        int vwidth = getWidth() - mPaddingLeft - mPaddingRight;
	        int vheight = getHeight() - mPaddingTop - mPaddingBottom;

	        boolean fits = (dwidth < 0 || vwidth == dwidth) &&
	                       (dheight < 0 || vheight == dheight);

	        if (dwidth <= 0 || dheight <= 0 || ScaleType.FIT_XY == mScaleType) {
	            /* If the drawable has no intrinsic size, or we're told to
	                scaletofit, then we just fill our entire view.
	            */
	            mDrawable.setBounds(0, 0, vwidth, vheight);
	            mDrawMatrix = null;
	        } else {
	            // We need to do the scaling ourself, so have the drawable
	            // use its native size.
	            mDrawable.setBounds(0, 0, dwidth, dheight);

	            if (ScaleType.MATRIX == mScaleType) {
	                // Use the specified matrix as-is.
	                if (mMatrix.isIdentity()) {
	                    mDrawMatrix = null;
	                } else {
	                    mDrawMatrix = mMatrix;
	                }
	            } else if (fits) {
	                // The bitmap fits exactly, no transform needed.
	                mDrawMatrix = null;
	            } else if (ScaleType.CENTER == mScaleType) {
	                // Center bitmap in view, no scaling.
	                mDrawMatrix = mMatrix;
	                mDrawMatrix.setTranslate((int) ((vwidth - dwidth) * 0.5f + 0.5f),
	                                         (int) ((vheight - dheight) * 0.5f + 0.5f));
	            } else if (ScaleType.CENTER_CROP == mScaleType) {
	                mDrawMatrix = mMatrix;

	                float scale;
	                float dx = 0, dy = 0;

	                if (dwidth * vheight > vwidth * dheight) {
	                    scale = (float) vheight / (float) dheight; 
	                    dx = (vwidth - dwidth * scale) * 0.5f;
	                } else {
	                    scale = (float) vwidth / (float) dwidth;
	                    dy = (vheight - dheight * scale) * 0.5f;
	                }
	                mDrawMatrix.setScale(scale, scale);
	                mDrawMatrix.postTranslate((int) (dx + 0.5f), (int) (dy + 0.5f));
	            } else if (ScaleType.CENTER_INSIDE == mScaleType) {
	                mDrawMatrix = mMatrix;
	                float scale;
	                float dx;
	                float dy;
	                
	                if (dwidth <= vwidth && dheight <= vheight) {
	                    scale = 1.0f;
	                } else {
	                    scale = Math.min((float) vwidth / (float) dwidth,
	                            (float) vheight / (float) dheight);
	                }
	                
	                dx = (int) ((vwidth - dwidth * scale) * 0.5f + 0.5f);
	                dy = (int) ((vheight - dheight * scale) * 0.5f + 0.5f);

	                mDrawMatrix.setScale(scale, scale);
	                mDrawMatrix.postTranslate(dx, dy);
	            } else {}
	        }
	    }
	/**
	 * Sets the CircularImageView's border width in pixels.
	 * 
	 * @param borderWidth
	 */
	public void setBorderWidth(int borderWidth)
	{
		this.borderWidth = borderWidth;
		this.requestLayout();
		this.invalidate();
	}
	
	/**
	 * Sets the CircularImageView's basic border color.
	 * 
	 * @param borderColor
	 */
	public void setBorderColor(int borderColor)
	{
		if (paintBorder != null)
			paintBorder.setColor(borderColor);
		this.invalidate();
	}
	
	/**
	 * Sets the color of the selector to be draw over the 
	 * CircularImageView. Be sure to provide some opacity.
	 * 
	 * @param selectorColor
	 */
	public void setSelectorColor(int selectorColor)
	{
		this.selectorFilter = new PorterDuffColorFilter(selectorColor, PorterDuff.Mode.SRC_ATOP);
		this.invalidate();
	}
	
	/**
	 * Sets the stroke width to be drawn around the CircularImageView 
	 * during click events when the selector is enabled.
	 * 
	 * @param selectorStrokeWidth
	 */
	public void setSelectorStrokeWidth(int selectorStrokeWidth)
	{
		this.selectorStrokeWidth = selectorStrokeWidth;
		this.requestLayout();
		this.invalidate();
	}
	
	/**
	 * Sets the stroke color to be drawn around the CircularImageView 
	 * during click events when the selector is enabled.
	 * 
	 * @param selectorStrokeColor
	 */
	public void setSelectorStrokeColor(int selectorStrokeColor)
	{
		if (paintSelectorBorder != null)
			paintSelectorBorder.setColor(selectorStrokeColor);
		this.invalidate();
	}
	public float getRadius(){
		return radius;
	}
	public void setRadius(float radius){
		this.radius=radius;
	//	this.invalidate();
	}
	public void setHeight(int value){
		ViewGroup.LayoutParams params = getLayoutParams();
		params.height = value;
		setLayoutParams(params);
		this.invalidate();
	}

	public void setWidth(int value){
		ViewGroup.LayoutParams params = getLayoutParams();
		params.width = value;
		setLayoutParams(params);
	//	this.invalidate();
	}
	
	/**
	 * Adds a dark shadow to this CircularImageView.
	 */
	public void addShadow()
	{
		setLayerType(LAYER_TYPE_SOFTWARE, paintBorder);
		paintBorder.setShadowLayer(4.0f, 0.0f, 2.0f, Color.BLACK);
	}
	
	
	@Override
	public void onDraw(Canvas canvas)
	{
		// Don't draw anything without an image
		if(image == null)
			return;
		
		// Nothing to draw (Empty bounds)
		if(image.getHeight() == 0 || image.getWidth() == 0)
			return;
		
		
		
		canvasSize = canvas.getWidth();
		if(canvas.getHeight() < canvasSize)
			canvasSize = canvas.getHeight();
		
		// Reinitialize shader, if necessary
	//	if(oldCanvasSize != canvasSize)
			refreshBitmapShader();
		
		// Apply shader to paint
			/*if(mDrawableWidth!=canvas.getWidth()||mDrawableHeight!=canvas.getHeight())
				return;*/
		paint.setShader(shader);
		
	
		
		// Draw the circular image itself
		
		rect.set(0+getPaddingLeft(), 0+getPaddingTop(), canvas.getWidth()-getPaddingRight(), canvas.getHeight()-getPaddingBottom());
		canvas.drawRoundRect(rect, radius, radius, paint);
		oldCanvasSize=canvasSize;
		//canvas.drawCircle(center + outerWidth, center + outerWidth, ((canvasSize - (outerWidth * 2)) / 2) - 4.0f, paint);
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent event)
	{
		// Check for clickable state and do nothing if disabled
		if(!this.isClickable()) {
			this.isSelected = false;
			return super.onTouchEvent(event);
		}
		
		// Set selected state based on Motion Event
		switch(event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				this.isSelected = true;
				break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_SCROLL:
			case MotionEvent.ACTION_OUTSIDE:
			case MotionEvent.ACTION_CANCEL:
				this.isSelected = false;
				break;
		}
		
		// Redraw image and return super type
		this.invalidate();
		return super.dispatchTouchEvent(event);
	}
	
	public void invalidate(Rect dirty) {
		super.invalidate(dirty);
			mDrawable = getDrawable();
			 mScaleType = getScaleType();
			 mMatrix=new Matrix();
			 Thread t= new Thread(new Runnable(){

					@Override
					public void run() {}});
				 t.setPriority(Thread.MIN_PRIORITY);
				 t.start();
			

				 if(getDrawable()!=null){
					 mDrawableWidth=mDrawable.getIntrinsicWidth();
					 mDrawableHeight=mDrawable.getIntrinsicHeight();
					 mPaddingLeft=getPaddingLeft();
					    mPaddingRight=getPaddingRight();
					    mPaddingTop=getPaddingTop();
					    mPaddingBottom=getPaddingBottom();
					    configureBounds() ;
					    if(image!=null){
					    //	image.recycle();
					    //	image=null;
					    }
					    
							
					drawableToBitmap(getDrawable());
					}
					if(shader != null || canvasSize > 0)
						refreshBitmapShader();
			
		
	
		   
		
	}
	
	public void invalidate(int l, int t, int r, int b) {
		super.invalidate(l, t, r, b);
	
			mDrawable = getDrawable();
			 mScaleType = getScaleType();
			 mMatrix=new Matrix();
			 Thread tr= new Thread(new Runnable(){

					@Override
					public void run() {}});
				 tr.setPriority(Thread.MIN_PRIORITY);
				 tr.start();
			 
			 

					if(getDrawable()!=null){
						 mDrawableWidth=mDrawable.getIntrinsicWidth();
						 mDrawableHeight=mDrawable.getIntrinsicHeight();
						 mPaddingLeft=getPaddingLeft();
						    mPaddingRight=getPaddingRight();
						    mPaddingTop=getPaddingTop();
						    mPaddingBottom=getPaddingBottom();
						    configureBounds() ;
						    if(image!=null){
						    //	image.recycle();
						    //	image=null;
						    }
						    
								
						 drawableToBitmap(getDrawable());
						}
						if(shader != null || canvasSize > 0)
							refreshBitmapShader();
				
		 
		   
	}
	
	@Override
	public void invalidate() {
		super.invalidate();
			mDrawable = getDrawable();
			 mScaleType = getScaleType();
			 mMatrix=new Matrix();
			 if(invalidateInBackground){
				 Thread t= new Thread(new Runnable(){

						@Override
						public void run() {
							if(getDrawable()!=null){
								 mDrawableWidth=mDrawable.getIntrinsicWidth();
								 mDrawableHeight=mDrawable.getIntrinsicHeight();
								 mPaddingLeft=getPaddingLeft();
								    mPaddingRight=getPaddingRight();
								    mPaddingTop=getPaddingTop();
								    mPaddingBottom=getPaddingBottom();
								    configureBounds() ;
								    if(image!=null){
								 //   	image.recycle();
								    //	image=null;
								    }
								    
										
								 drawableToBitmap(getDrawable());
								}
								if(shader != null || canvasSize > 0)
									refreshBitmapShader();
						}});
					 t.setPriority(Thread.MIN_PRIORITY);
					 t.start();
			 }
			 else{
				 if(getDrawable()!=null){
					 mDrawableWidth=mDrawable.getIntrinsicWidth();
					 mDrawableHeight=mDrawable.getIntrinsicHeight();
					 mPaddingLeft=getPaddingLeft();
					    mPaddingRight=getPaddingRight();
					    mPaddingTop=getPaddingTop();
					    mPaddingBottom=getPaddingBottom();
					    configureBounds() ;
					    if(image!=null){
					   // 	image.recycle();
					  //  	image=null;
					    }
					    
							
					 drawableToBitmap(getDrawable());
					}
					if(shader != null || canvasSize > 0)
						refreshBitmapShader();
			 }
			 

					
				
			 
		
		   
		
		
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		int width = measureWidth(widthMeasureSpec);
		int height = measureHeight(heightMeasureSpec);
		setMeasuredDimension(width, height);
	}
	

	private int measureWidth(int measureSpec)
	{
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		if (specMode == MeasureSpec.EXACTLY) {
			// The parent has determined an exact size for the child.
			result = specSize;
		}
		else {
			// The child can be as large as it wants up to the specified size.
			result = specSize;
		}
		

		return result;
	}

	private int measureHeight(int measureSpecHeight)
	{
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpecHeight);
		int specSize = MeasureSpec.getSize(measureSpecHeight);

		if (specMode == MeasureSpec.EXACTLY) {
			// We were told how big to be
			result = specSize;
		} else  {
			// The child can be as large as it wants up to the specified size.
			result = specSize;
		} 

		return (result + 2);
	}
	private Bitmap bitmap;
	/**
	 * Convert a drawable object into a Bitmap
	 * 
	 * @param drawable
	 * @return
	 */
	private  Canvas canvas;
	public void drawableToBitmap(Drawable drawable)
	{
		if (drawable == null||mDrawableWidth<=0||mDrawableHeight<=0||getWidth()<=0||getHeight()<=0) { // Don't do anything without a proper drawable
			
		}
		
		
		else if(getWidth()>1&&getHeight()>1){
		image = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
         if(!image.isRecycled()){
        	  canvas = new Canvas(image);
        		//	drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        		
        			 int saveCount = canvas.getSaveCount();
        	         canvas.save();
        	        canvas.translate(mPaddingLeft, mPaddingTop);
        	         if(mDrawMatrix!=null){
        	          canvas.concat(mDrawMatrix);
        	       }
     	mDrawable.draw(canvas);
     	  canvas.restoreToCount(saveCount);
     	}
		}
		//return void;
	}
	
	/**
	 * Reinitializes the shader texture used to fill in 
	 * the Circle upon drawing.
	 */
	public void refreshBitmapShader()
	{
		mDrawable = getDrawable();
		 mScaleType = getScaleType();
	
		 mMatrix.reset();
		 if(mDrawable!=null){
		 mDrawableWidth=mDrawable.getIntrinsicWidth();
		 mDrawableHeight=mDrawable.getIntrinsicHeight();
		 mPaddingLeft=getPaddingLeft();
		    mPaddingRight=getPaddingRight();
		    mPaddingTop=getPaddingTop();
		    mPaddingBottom=getPaddingBottom();
		    configureBounds() ;
		   
		    drawableToBitmap(getDrawable());
		    if(image!=null){
		    		shader = new BitmapShader(image, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
		    }
	
		 }
	}
	
	/**
	 * Returns whether or not this view is currently 
	 * in its selected state.
	 */
	public boolean isSelected()
	{
		return this.isSelected;
	}

	/**
	 * @return the position
	 */
	public int getPosition() {
		return position;
	}

	/**
	 * @param position the position to set
	 */
	public void setPosition(int position) {
		this.position = position;
	}

	/**
	 * @return the invalidateInBackground
	 */
	public boolean isInvalidateInBackground() {
		return invalidateInBackground;
	}

	/**
	 * @param invalidateInBackground the invalidateInBackground to set
	 */
	public void setInvalidateInBackground(boolean invalidateInBackground) {
		this.invalidateInBackground = invalidateInBackground;
	}
}