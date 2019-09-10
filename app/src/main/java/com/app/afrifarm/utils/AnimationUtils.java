package com.app.afrifarm.utils;


import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class AnimationUtils {

	public static ObjectAnimator getObjectAniRepeat(Object target,String propertyName,long duration,TimeInterpolator interpolation,float... values){
		ObjectAnimator ob=ObjectAnimator.ofFloat(target, propertyName,values);
		ob.setDuration(duration);
		ob.setInterpolator(interpolation);
		ob.setRepeatCount(ObjectAnimator.INFINITE);
		ob.setRepeatMode(ObjectAnimator.REVERSE);
		return ob;
	}
   public static ObjectAnimator getObjectAni(Object target,String propertyName,long duration,TimeInterpolator interpolation,float... values){
	ObjectAnimator ob=ObjectAnimator.ofFloat(target, propertyName,values);
			ob.setDuration(duration);
	ob.setInterpolator(interpolation);
	return ob;
    }
   public static ObjectAnimator getObjectAniOfInt(Object target,String propertyName,long duration,TimeInterpolator interpolation,int... values){
		ObjectAnimator ob=ObjectAnimator.ofInt(target, propertyName,values);
				ob.setDuration(duration);
		ob.setInterpolator(interpolation);
		return ob;
	    }

   public static ValueAnimator getColorAni(final View target,int fromColor,int toColor,int duration){
	ValueAnimator anim= ValueAnimator.ofObject(new ArgbEvaluator(),fromColor,toColor);
	anim.setDuration(duration);
	anim.addUpdateListener(new AnimatorUpdateListener(){

		@Override
		public void onAnimationUpdate(ValueAnimator animation) {
			// TODO Auto-generated method stub
			if(target instanceof ImageView){
				((ImageView)target).setColorFilter((Integer) animation.getAnimatedValue());
			}
			else{

				target.setBackgroundColor((Integer) animation.getAnimatedValue());	
			}
		}});
	   return anim;
     }
   public static ValueAnimator getBackgroundColorAni(final View target,int fromColor,int toColor,int duration){
	ValueAnimator anim= ValueAnimator.ofObject(new ArgbEvaluator(),fromColor,toColor);
	anim.setDuration(duration);
	anim.addUpdateListener(new AnimatorUpdateListener(){

		@Override
		public void onAnimationUpdate(ValueAnimator animation) {
			// TODO Auto-generated method stub
			target.setBackgroundColor((Integer) animation.getAnimatedValue());	
			
		}});
	   return anim;
     }
   public static ValueAnimator getTextColorAni(final TextView target,int fromColor,int toColor,int duration){
		ValueAnimator anim= ValueAnimator.ofObject(new ArgbEvaluator(),fromColor,toColor);
		anim.setDuration(duration);
		anim.addUpdateListener(new AnimatorUpdateListener(){

			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				// TODO Auto-generated method stub
				target.setTextColor((Integer) animation.getAnimatedValue());
			}});
		   return anim;
	     }
   public static AnimatorSet createTogetherSet(Animator... items){
		AnimatorSet firstSet= new AnimatorSet();
		firstSet.playTogether(items);
		return firstSet;
	}
	public static AnimatorSet createSeqSet(Animator... items){
		AnimatorSet firstSet= new AnimatorSet();
		firstSet.playSequentially(items);
		return firstSet;
	}
}
