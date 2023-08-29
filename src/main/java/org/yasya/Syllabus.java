package org.yasya;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;
import javax.swing.SwingWorker;
import org.json.JSONArray;
import org.json.JSONObject;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Syllabus extends SwingWorker<Void, Integer> {
	public double bestScore = 9999998;
	public Solution bestSolution = null;
	private long startTime = System.currentTimeMillis();
	public double[] history = new double[Config.HISTORY_COUNT];
	public int historyIndex = 0;
	public int counter = 0;

	public record Row(String group, String course, String teacher){}

	public Syllabus(){}

	synchronized public void addHistory(boolean jumped, double newScore) {
		if(jumped){
			history[historyIndex] = newScore;
		}
		else{
			history[historyIndex] = 0;
		}
		historyIndex = (historyIndex + 1) % history.length;
		if(historyIndex == 0) {
			Utils.updateChart(history);
		}
	}

	public class Config {
		public static final String[] DAYS = new String[] {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
		public static final int MAX_LESSONS_COUNT = 5;
		public static final int PARALLEL = 5;
		public static final String[] BUILDINGS = new String[] {"Tech", "Hum", "Phy"};
		public static final String[] ROOMS = new String[] {"108", "217", "305", "404", "1408","1", "2", "3", "4", "5", "Gym", "Pool"};
		public static final String[] GROUPS = new String[] {"it12", "it22", "tk32", "tk42", "ap12", "ap22", "sp32"};
		public static final String[] COURSES = new String[] {"Math", "English", "Java", "Art", "Volleyball", "Swimming"};
		public static final String[] TEACHERS = new String[] {"Satoru", "Suguru", "Nakahara", "Osamu", "Doppo", "Poe","Euler", "Gosling", "Klimt", "Ukai", "Madeleine"};
		public static final String CONTENT_STR = 
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
		public static final JSONObject CONTENT_JSON = new JSONObject(CONTENT_STR);
		public static final String TEACHER_SKILLS_STR = 
		"""
			{
				'Math':['Doppo','Gosling', 'Nakahara', 'Osamu', 'Euler'],
				'Java':['Doppo','Gosling', 'Nakahara', 'Osamu', 'Satoru'],
				'English':['Poe', 'Nakahara', 'Osamu', 'Suguru'],
				'Art':['Klimt', 'Nakahara', 'Osamu'],
				'Volleyball':['Ukai', 'Nakahara', 'Osamu'],
				'Swimming':['Madeleine', 'Nakahara', 'Osamu']
			}
		""".replace("'", "\"");
		public static final JSONObject TEACHER_SKILLS_JSON = new JSONObject(TEACHER_SKILLS_STR);
		public static final double INITIAL_T = 0.27;
		public static final int HISTORY_COUNT = 100000;
	}
	
	public class Solution implements Chainable, Runnable {
		public Row[][][] rows;
		public double score;
		public ThreadLocalRandom localRandom;

		public Solution(Row[][][] rows, double score) {
			this.rows = new Row[Config.DAYS.length][Config.MAX_LESSONS_COUNT][Config.ROOMS.length];
			for(int day = 0; day < Config.DAYS.length; day++) {
				for(int time = 0; time < Config.MAX_LESSONS_COUNT; time++) {
					for(int room = 0; room < Config.ROOMS.length; room++) {
						this.rows[day][time][room] = rows[day][time][room];
					}
				}
			}
			this.score = score;
			this.localRandom = ThreadLocalRandom.current();
		}

		public Solution() {
			this.localRandom = ThreadLocalRandom.current();
			this.rows = new Row[Config.DAYS.length][Config.MAX_LESSONS_COUNT][Config.ROOMS.length];
			Iterator<String> groupsKeys = Config.CONTENT_JSON.keys();
			while (groupsKeys.hasNext()) {
				String group = groupsKeys.next();
				JSONObject courses = Config.CONTENT_JSON.getJSONObject(group);
				Iterator<String> coursesKeys = courses.keys();
				while(coursesKeys.hasNext()) {
					String course = coursesKeys.next();
					int number = courses.getInt(course);
					
					JSONArray teachers = Config.TEACHER_SKILLS_JSON.getJSONArray(course);
					int teacherNumber = localRandom.nextInt(teachers.length());
					var teacher = (String)(teachers.get(teacherNumber));
					for(int i = 0; i < number; i++) {
						int day;
						int time;
						int room;
						do{
							day = localRandom.nextInt(Config.DAYS.length);
							time = localRandom.nextInt(Config.MAX_LESSONS_COUNT);
							room = localRandom.nextInt(Config.ROOMS.length);
						} while(rows[day][time][room] != null);
						this.rows[day][time][room] = new Row(group, course, teacher);
					}
				}
			}
			this.calculateScore();
		}

		public String toString() {
			StringBuilder sb = new StringBuilder();
			for (int day = 0; day < Config.DAYS.length; day++) {
				sb.append(Config.DAYS[day] + '\n');
				for (int room = 0; room < Config.ROOMS.length; room++) {
					for (int time = 0; time < Config.MAX_LESSONS_COUNT; time++) {
						Row row = rows[day][time][room];
						if(row == null) {
							sb.append(String.format("%4s %2d \n", Config.ROOMS[room], time + 1));
						}
						else {
							sb.append(String.format("%4s %2d %6s %10s %10s \n", Config.ROOMS[room], time + 1, row.group, row.course, row.teacher));
						}
					}
				}
			}
			return sb.toString();
		}

		@Override
		public void afterJump(boolean jumped, double newScore) {
			addHistory(jumped, newScore);
		}

		@Override
		public void afterBetterSolutionFound(Chainable s, double score, double t) {
			setBest((Solution)s, score, t);
		}

		public void calculateScore() {
			int score = 0;
			for(int day = 0; day < Config.DAYS.length; day++){
				for(int time = 0; time < Config.MAX_LESSONS_COUNT; time++){
					Map<String, Integer> groups = new HashMap<>();
					Map<String, Integer> teachers = new HashMap<>();
					for(int room = 0; room < Config.ROOMS.length; room++){
						Row row = rows[day][time][room];
						if(row == null) continue;
						if(!groups.containsKey(row.group)){
							groups.put(row.group, 0);
						}
						groups.put(row.group, groups.get(row.group) + 1);

						if(!teachers.containsKey(row.teacher)){
							teachers.put(row.teacher, 0);
						}
						teachers.put(row.teacher, teachers.get(row.teacher) + 1);
					}
					for (Integer value : groups.values()){
						score += (value - 1);
					}
					for (Integer value : teachers.values()){
						score += (value - 1);
					}
				}
			}
			this.score = score;
		}

		@Override
		public Chainable next() {
			Solution s = copy();
			if(localRandom.nextInt(5) > 0) {
				int day1;
				int time1;
				int room1;

				int day2;
				int time2;
				int room2;

				do{
					day1 = localRandom.nextInt(Config.DAYS.length);
					time1 = localRandom.nextInt(Config.MAX_LESSONS_COUNT);
					room1 = localRandom.nextInt(Config.ROOMS.length);

					day2 = localRandom.nextInt(Config.DAYS.length);
					time2 = localRandom.nextInt(Config.MAX_LESSONS_COUNT);
					room2 = localRandom.nextInt(Config.ROOMS.length);
				} while(s.rows[day1][time1][room1] == s.rows[day2][time2][room2]);

				var temp = s.rows[day1][time1][room1];
				s.rows[day1][time1][room1] = rows[day2][time2][room2];
				s.rows[day2][time2][room2] = temp;
			}
			else {
				int day;
				int time;
				int room;

				do{
					day = localRandom.nextInt(Config.DAYS.length);
					time = localRandom.nextInt(Config.MAX_LESSONS_COUNT);
					room = localRandom.nextInt(Config.ROOMS.length);
				} while(s.rows[day][time][room] == null);
			}
			
			s.calculateScore();
			return s;
		}

		@Override
		public double score() {
			return score;
		}

		public Solution copy() {
			Row[][][] rowsCopy = new Row[Config.DAYS.length][Config.MAX_LESSONS_COUNT][Config.ROOMS.length];
			for(int day = 0; day < Config.DAYS.length; day++) {
				for(int time = 0; time < Config.MAX_LESSONS_COUNT; time++) {
					for(int room = 0; room < Config.ROOMS.length; room++) {
						rowsCopy[day][time][room] = this.rows[day][time][room];
					}
				}
			}
			Solution s = new Solution(rowsCopy, this.score);
			return s;
		}

		public void run() {
			try {
				Utils.fire(this);
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public synchronized void onProgress(int progress) {
			publish(progress);
		}

	}
	
	public synchronized void setBest(Solution newSolution, double newScore, double t) {
		if(newScore < bestScore) {
			bestScore = newScore;
			bestSolution = newSolution.copy();
			System.out.printf("better solution found: %8.1f %8.5f \n", bestScore, t);
			BufferedImage image = SyllabusPNG.getAreaImage(340, 400, bestSolution.rows);
			UI.areaIcon.setImage(image);
			UI.scoreLabel.setText(String.format("better solution found: %6.1f", bestScore));
			UI.areaLabel.repaint();
		}
	}

	@Override
	protected Void doInBackground() throws Exception {
		UI.currentTemperature = Config.INITIAL_T;
		UI.temperatureLabel.setText("t = " + Double.toString(Config.INITIAL_T));
		publish(0);
		Solution initialSolution = new Solution();
		Thread[] sh = Stream.generate(() -> new Thread(initialSolution.copy()))
			.limit(Config.PARALLEL)
			.toArray(Thread[]::new);

		Arrays.stream(sh).forEach(Thread::start);
		Arrays.stream(sh).forEach(t -> {
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}); 
		
		publish(100);

		return null;
	}

	@Override
	protected void process(java.util.List<Integer> chunks) {
		int latestProgress = chunks.get(chunks.size() - 1);
		UI.progressBar.setValue(latestProgress);
	}

	@Override
	protected void done() {
		long duration = System.currentTimeMillis() - startTime;
		System.out.printf("best score: %f, duration: %d ms", bestScore, duration);
		try (BufferedWriter writer = new BufferedWriter(new FileWriter("Syllabus.txt"))) {
			writer.write(bestSolution.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

