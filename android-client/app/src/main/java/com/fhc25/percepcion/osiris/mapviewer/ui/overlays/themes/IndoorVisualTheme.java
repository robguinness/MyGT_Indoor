/**
Copyright 2015 Osiris Project Team

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/   

package com.fhc25.percepcion.osiris.mapviewer.ui.overlays.themes;

import android.graphics.Color;

/**
 * Theme of the whole indoor representation
 */
public class IndoorVisualTheme {

	protected IndoorElementTheme doorTheme = new IndoorElementTheme(1.0f,
			Color.rgb(241, 148, 138), Color.WHITE, 1);

	protected IndoorElementTheme mainDoorTheme = new IndoorElementTheme(2.0f,
			Color.GREEN, Color.WHITE, 1);

	protected IndoorElementTheme exitDoorTheme = new IndoorElementTheme(2.0f,
			Color.RED, Color.WHITE, 1);

	protected IndoorElementTheme shellTheme = new IndoorElementTheme(0f,
			Color.WHITE, Color.rgb(233, 127, 19), 30);

	protected IndoorElementTheme officeTheme = new IndoorElementTheme(0f,
			Color.rgb(255, 248, 235), Color.rgb(238, 226, 214), 20);

	protected IndoorElementTheme corridorTheme = new IndoorElementTheme(0f,
			Color.WHITE, Color.rgb(238, 226, 214), 10);

	protected IndoorElementTheme stairwayTheme = new IndoorElementTheme(0f,
			Color.rgb(255, 243, 145), Color.rgb(238, 226, 214), 20);

	protected IndoorElementTheme elevatorTheme = new IndoorElementTheme(0f,
			Color.rgb(210, 210, 210), Color.rgb(238, 226, 214), 20);

	protected IndoorElementTheme roomTheme = new IndoorElementTheme(0f,
			Color.rgb(174, 214, 241), Color.DKGRAY, 10);

	protected IndoorElementTheme toiletTheme = new IndoorElementTheme(0f,
			Color.rgb(255, 255, 0), Color.DKGRAY, 15);

	protected IndoorElementTheme tinyBuildingTheme = new IndoorElementTheme(
			0f, Color.rgb(255, 248, 235), Color.rgb(233, 127, 19), 4);

	/**
	 * Gets the doors theme
	 * 
	 * @return theme
	 */
	public IndoorElementTheme getDoorTheme() {
		return doorTheme;
	}

	/**
	 * Gets the main entrance doors theme
	 *
	 * @return theme
	 */
	public IndoorElementTheme getMainDoorTheme() {
		return mainDoorTheme;
	}

	/**
	 * Gets the exit doors theme
	 *
	 * @return theme
	 */
	public IndoorElementTheme getExitDoorTheme() {
		return exitDoorTheme;
	}

	/**
	 * Gets the shells theme
	 * 
	 * @return theme
	 */
	public IndoorElementTheme getShellTheme() {
		return shellTheme;
	}

	/**
	 * Gets the offices theme
	 * 
	 * @return theme
	 */
	public IndoorElementTheme getOfficeTheme() {
		return officeTheme;
	}

	/**
	 * Gets the corridors theme
	 * 
	 * @return theme
	 */
	public IndoorElementTheme getCorridorTheme() {
		return corridorTheme;
	}

	/**
	 * Gets the stairways theme
	 * 
	 * @return theme
	 */
	public IndoorElementTheme getStairwayTheme() {
		return stairwayTheme;
	}

	/**
	 * Gets the elevators theme
	 * 
	 * @return theme
	 */
	public IndoorElementTheme getElevatorTheme() {
		return elevatorTheme;
	}

	/**
	 * Gets the rooms theme
	 * 
	 * @return theme
	 */
	public IndoorElementTheme getRoomTheme() {
		return roomTheme;
	}

	/**
	 * Gets the toilets theme
	 *
	 * @return theme
	 */
	public IndoorElementTheme getToiletTheme() {
		return toiletTheme;
	}

	/**
	 * Gets the "tiny" building theme
	 * 
	 * @return theme
	 */
	public IndoorElementTheme getTinyBuildingTheme() {
		return tinyBuildingTheme;
	}
}
