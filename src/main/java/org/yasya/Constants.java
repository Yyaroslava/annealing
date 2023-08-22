package org.yasya;

import org.json.JSONObject;

public final class Constants {
	public static final int TETRIS_BOARD_WIDTH = 16;
	public static final int TETRIS_BOARD_HEIGHT = 16;
	public static final int TETRIS_MAX_TILE_SIZE = 7;
	public static int TETRIS_TILES_COUNT;
	public static final int TETRIS_PARALLEL = 14;
	public static final long TETRIS_STEP_COUNT = 100000;
	public static final double TETRIS_INITIAL_T = 0.5;
	public static final int[][] TETRIS_PALETTE = new int[][] {
		{255, 0, 0},
		{255, 165, 0},
		{255, 255, 0},
		{0, 128, 0},
		{0, 0, 255},
		{75, 0, 130},
		{238, 130, 238}
	};

	public static final int SALESMAN_TOWNS_COUNT = 100;
	public static final int SALESMAN_PARALLEL = 14;
	public static final long SALESMAN_STEP_COUNT = 100000000;
	public static final double SALESMAN_INITIAL_T = 10;

	public static final String[] SYLLABUS_DAYS = new String[] {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
	public static final int SYLLABUS_MAX_LESSONS_COUNT = 5;
	public static final String[] SYLLABUS_BUILDINGS = new String[] {"Tech", "Hum", "Phy"};
	public static final String[] SYLLABUS_ROOMS = new String[] {"108", "217", "305","1", "2", "3", "Gym", "Pool"};
	public static final String[] SYLLABUS_GROUPS = new String[] {"it12", "it22", "tk32", "tk42", "ap12", "ap22", "sp32"};
	public static final String[] SYLLABUS_COURSES = new String[] {"Math", "English", "Java", "Art", "Volleyball", "Swimming"};
	public static final String[] SYLLABUS_TEACHERS = new String[] {"Doppo", "Poe", "Gosling", "Klimt", "Ukai", "Madeleine"};
	public static final String SYLLABUS_CONTENT_STR = 
	"""
		{
			'it12':{
				'Math':6,
				'English':2,
				'Java':10,
				'Art':1,
				'Volleyball':2,
				'Swimming':1
			},
			'it22':{
				'Math':6,
				'English':3,
				'Java':8,
				'Art':2,
				'Volleyball':2,
				'Swimming':1
			},
			'tk32':{
				'Math':8,
				'English':3,
				'Java':6,
				'Art':1,
				'Volleyball':2,
				'Swimming':1
			},
			'tk42':{
				'Math':10,
				'English':3,
				'Java':5,
				'Art':1,
				'Volleyball':2,
				'Swimming':1
			},
			'ap12':{
				'Math':2,
				'English':6,
				'Java':1,
				'Art':10,
				'Volleyball':1,
				'Swimming':2
			},
			'ap22':{
				'Math':2,
				'English':10,
				'Java':1,
				'Art':6,
				'Volleyball':1,
				'Swimming':2
			},
			'sp32':{
				'Math':2,
				'English':2,
				'Java':0,
				'Art':1,
				'Volleyball':8,
				'Swimming':10
			}
		}
	""".replace("'", "\"");
	public static final JSONObject SYLLABUS_CONTENT_JSON = new JSONObject(SYLLABUS_CONTENT_STR);
	public static final String SYLLABUS_TEACHER_SKILLS_STR = 
	"""
		{
			'Math':['Doppo','Gosling'],
			'Java':['Doppo','Gosling'],
			'English':['Poe'],
			'Art':['Klimt'],
			'Volleyball':['Ukai'],
			'Swimming':['Madeleine']
		}
	""".replace("'", "\"");
	public static final JSONObject SYLLABUS_TEACHER_SKILLS_JSON = new JSONObject(SYLLABUS_TEACHER_SKILLS_STR);
	public static final long SYLLABUS_STEP_COUNT = 100000;
	public static final double SYLLABUS_INITIAL_T = 10;

}
