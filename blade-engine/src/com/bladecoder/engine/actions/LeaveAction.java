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
package com.bladecoder.engine.actions;

import java.util.HashMap;

import com.bladecoder.engine.actions.Param.Type;
import com.bladecoder.engine.model.World;


public class LeaveAction implements Action, ActionCallback {
	public static final String INFO = "Change the current scene.";
	public static final Param[] PARAMS = {
		new Param("scene", "The target scene", Type.SCENE, true),
	};		
	
	String scene;

	@Override
	public boolean run(ActionCallback cb) {
		
		// Queue the setCurrentScene to execute at the end of world update
		ActionCallbackQueue.add(this);
		
		return false;
	}
	
	@Override
	public void resume() {
		World.getInstance().setCurrentScene(scene);
	}

	@Override
	public void setParams(HashMap<String, String> params) {
		scene = params.get("scene");
	}


	@Override
	public String getInfo() {
		return INFO;
	}

	@Override
	public Param[] getParams() {
		return PARAMS;
	}

}
