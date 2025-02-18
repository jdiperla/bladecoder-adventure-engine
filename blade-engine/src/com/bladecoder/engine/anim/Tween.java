/*******************************************************************************
 * Copyright 2014 Rafael Garcia Moreno.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.bladecoder.engine.anim;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;
import com.bladecoder.engine.actions.ActionCallback;
import com.bladecoder.engine.actions.ActionCallbackQueue;
import com.bladecoder.engine.util.ActionCallbackSerialization;
import com.bladecoder.engine.util.InterpolationUtils;

public class Tween implements Serializable {
	public final static int NO_REPEAT = 0;
	public final static int REPEAT = 1;
	public final static int PINGPONG = 2;
	public final static int REVERSE = 3;
	public final static int REVERSE_REPEAT = 4;
	public final static int FROM_FA = 5;

	public final static int INFINITY = -1;

	private float duration, time;
	private Interpolation interpolation;
	private boolean reverse, began, complete;
	private int type;
	private int count;

	private ActionCallback cb;
	private String cbSer = null;

	public Tween() {
	}

	public void update(float delta) {
		if (complete)
			return;

		if (!began) {
			began = true;
		}

		time += delta;

		if (time >= duration) {
			if (type == NO_REPEAT || type == REVERSE || count == 1) {
				complete = true;
			} else if (count != 1) {
				complete = false;
				count--;
				time = 0;

				if (type == PINGPONG)
					reverse = !reverse;
			}
		}

		if (complete) {
			callCb();
		}
	}
	
	private void callCb() {
		if(cb != null || cbSer != null) {
			if(cbSer != null) {
				cb = ActionCallbackSerialization.find(cbSer);
				cbSer = null;
			}
			
			ActionCallbackQueue.add(cb);
		}
	}

	public float getPercent() {
		float percent;
		if (complete) {
			percent = 1;
		} else {
			percent = time / duration;
			if (interpolation != null)
				percent = interpolation.apply(percent);
		}

		return (reverse ? 1 - percent : percent);
	}

	/** Skips to the end of the transition. */
	public void finish() {
		time = duration;
	}

	public void restart() {
		time = 0;
		began = false;
		complete = false;
	}

	public void reset() {
		reverse = false;
		interpolation = null;
	}

	/** Gets the transition time so far. */
	public float getTime() {
		return time;
	}

	/** Sets the transition time so far. */
	public void setTime(float time) {
		this.time = time;
	}

	public float getDuration() {
		return duration;
	}

	/** Sets the length of the transition in seconds. */
	public void setDuration(float duration) {
		this.duration = duration;
	}

	public Interpolation getInterpolation() {
		return interpolation;
	}

	public void setInterpolation(Interpolation interpolation) {
		this.interpolation = interpolation;
	}

	public boolean isReverse() {
		return reverse;
	}

	/** When true, the action's progress will go from 100% to 0%. */
	public void setReverse(boolean reverse) {
		this.reverse = reverse;
	}

	public void setCb(ActionCallback cb) {
		this.cb = cb;
	}

	public boolean isComplete() {
		return complete;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;

		if (type == REVERSE || type == REVERSE_REPEAT)
			reverse = true;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	@Override
	public void write(Json json) {
		json.writeValue("duration", duration);
		json.writeValue("time", time);
		json.writeValue("reverse", reverse);
		json.writeValue("began", began);
		json.writeValue("complete", complete);
		json.writeValue("type", type);
		json.writeValue("count", count);
		
		String i = null;
		
		if(interpolation != null)
			i = InterpolationUtils.getName(interpolation);
		
		json.writeValue("interpolation", i);
		
		if(cbSer != null)
			json.writeValue("cb", cbSer);
		else
			json.writeValue("cb", ActionCallbackSerialization.find(cb),
				cb == null ? null : String.class);
	}

	@Override
	public void read(Json json, JsonValue jsonData) {
		duration = json.readValue("duration", Float.class, jsonData);
		time = json.readValue("time", Float.class, jsonData);;
		reverse = json.readValue("reverse", Boolean.class, jsonData);
		began = json.readValue("began", Boolean.class, jsonData);
		complete = json.readValue("complete", Boolean.class, jsonData);
		type = json.readValue("type", Integer.class, jsonData);
		count = json.readValue("count", Integer.class, jsonData);
		
		String i = json.readValue("interpolation", String.class, jsonData);
		
		if(i != null)
			interpolation = InterpolationUtils.getInterpolation(i);

		cbSer = json.readValue("cb", String.class, jsonData);
	}
}
