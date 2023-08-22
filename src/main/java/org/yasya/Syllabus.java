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

public class Syllabus extends SwingWorker<Void, Integer> {
	public double bestScore = 9999998;
	public Solution bestSolution = null;
	private long startTime = System.currentTimeMillis();

	public record Row(String group, String course, String teacher){}

	public Syllabus(){}
	
	public class Solution implements Chainable, Runnable {
		public Row[][][] rows;
		public double score;
		public ThreadLocalRandom localRandom;

		public Solution(Row[][][] rows, double score) {
			this.rows = new Row[Constants.SYLLABUS_DAYS.length][Constants.SYLLABUS_MAX_LESSONS_COUNT][Constants.SYLLABUS_ROOMS.length];
			for(int day = 0; day < Constants.SYLLABUS_DAYS.length; day++) {
				for(int time = 0; time < Constants.SYLLABUS_MAX_LESSONS_COUNT; time++) {
					for(int room = 0; room < Constants.SYLLABUS_ROOMS.length; room++) {
						this.rows[day][time][room] = rows[day][time][room];
					}
				}
			}
			this.score = score;
			this.localRandom = ThreadLocalRandom.current();
		}

		public Solution() {
			this.localRandom = ThreadLocalRandom.current();
			this.rows = new Row[Constants.SYLLABUS_DAYS.length][Constants.SYLLABUS_MAX_LESSONS_COUNT][Constants.SYLLABUS_ROOMS.length];
			Iterator<String> groupsKeys = Constants.SYLLABUS_CONTENT_JSON.keys();
			while (groupsKeys.hasNext()) {
				String group = groupsKeys.next();
				JSONObject courses = Constants.SYLLABUS_CONTENT_JSON.getJSONObject(group);
				Iterator<String> coursesKeys = courses.keys();
				while(coursesKeys.hasNext()) {
					String course = coursesKeys.next();
					int number = courses.getInt(course);
					//System.out.printf("%s %s %d \n", group, course, number);
					
					JSONArray teachers = Constants.SYLLABUS_TEACHER_SKILLS_JSON.getJSONArray(course);
					int teacherNumber = localRandom.nextInt(teachers.length());
					var teacher = (String)(teachers.get(teacherNumber));
					for(int i = 0; i < number; i++) {
						int day;
						int time;
						int room;
						do{
							day = localRandom.nextInt(Constants.SYLLABUS_DAYS.length);
							time = localRandom.nextInt(Constants.SYLLABUS_MAX_LESSONS_COUNT);
							room = localRandom.nextInt(Constants.SYLLABUS_ROOMS.length);
						} while(rows[day][time][room] != null);
						this.rows[day][time][room] = new Row(group, course, teacher);
						//System.out.printf("%d %d %d %s %s %s \n", day, time, room, group, course, teacher);
					}
				}
			}
			this.calculateScore();
		}

		public String toString() {
			StringBuilder sb = new StringBuilder();
			for (int day = 0; day < Constants.SYLLABUS_DAYS.length; day++) {
				sb.append(Constants.SYLLABUS_DAYS[day] + '\n');
				for (int room = 0; room < Constants.SYLLABUS_ROOMS.length; room++) {
					for (int time = 0; time < Constants.SYLLABUS_MAX_LESSONS_COUNT; time++) {
						Row row = rows[day][time][room];
						if(row == null) {
							sb.append(String.format("%4s %2d \n", Constants.SYLLABUS_ROOMS[room], time + 1));
						}
						else {
							sb.append(String.format("%4s %2d %6s %10s %10s \n", Constants.SYLLABUS_ROOMS[room], time + 1, row.group, row.course, row.teacher));
						}
					}
				}
			}
			return sb.toString();
		}

		@Override
		public void afterBetterSolutionFound(Chainable s, double score, double t) {
			setBest((Solution)s, score, t);
		}

		public void calculateScore() {
			int score = 0;
			for(int day = 0; day < Constants.SYLLABUS_DAYS.length; day++){
				for(int time = 0; time < Constants.SYLLABUS_MAX_LESSONS_COUNT; time++){
					Map<String, Integer> groups = new HashMap<>();
					Map<String, Integer> teachers = new HashMap<>();
					for(int room = 0; room < Constants.SYLLABUS_ROOMS.length; room++){
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
					day1 = localRandom.nextInt(Constants.SYLLABUS_DAYS.length);
					time1 = localRandom.nextInt(Constants.SYLLABUS_MAX_LESSONS_COUNT);
					room1 = localRandom.nextInt(Constants.SYLLABUS_ROOMS.length);

					day2 = localRandom.nextInt(Constants.SYLLABUS_DAYS.length);
					time2 = localRandom.nextInt(Constants.SYLLABUS_MAX_LESSONS_COUNT);
					room2 = localRandom.nextInt(Constants.SYLLABUS_ROOMS.length);
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
					day = localRandom.nextInt(Constants.SYLLABUS_DAYS.length);
					time = localRandom.nextInt(Constants.SYLLABUS_MAX_LESSONS_COUNT);
					room = localRandom.nextInt(Constants.SYLLABUS_ROOMS.length);
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
			Row[][][] rowsCopy = new Row[Constants.SYLLABUS_DAYS.length][Constants.SYLLABUS_MAX_LESSONS_COUNT][Constants.SYLLABUS_ROOMS.length];
			for(int day = 0; day < Constants.SYLLABUS_DAYS.length; day++) {
				for(int time = 0; time < Constants.SYLLABUS_MAX_LESSONS_COUNT; time++) {
					for(int room = 0; room < Constants.SYLLABUS_ROOMS.length; room++) {
						rowsCopy[day][time][room] = this.rows[day][time][room];
					}
				}
			}
			Solution s = new Solution(rowsCopy, this.score);
			return s;
		}

		public void run() {
			try {
				Utils.fire(this, Constants.SYLLABUS_INITIAL_T, Constants.SYLLABUS_STEP_COUNT);
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
	
	public void saveBestSolution() {
		SyllabusPNG.saveArea(bestSolution.rows, "area.png");
	}

	public synchronized void setBest(Solution newSolution, double newScore, double t) {
		if(newScore < bestScore) {
			bestScore = newScore;
			bestSolution = newSolution.copy();
			System.out.printf("better solution found: %8.1f %8.5f \n", bestScore, t);
			saveBestSolution();
			UI.areaIcon.getImage().flush();
			UI.areaLabel.repaint();
		}
	}

	@Override
	protected Void doInBackground() throws Exception {
		publish(0);
		Solution initialSolution = new Solution();
		Thread[] sh = Stream.generate(() -> new Thread(initialSolution.copy()))
			.limit(Constants.SYLLABUS_PARALLEL)
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
		UI.areaIcon.getImage().flush();
		UI.areaLabel.repaint();
		long duration = System.currentTimeMillis() - startTime;
		System.out.printf("best score: %f, duration: %d ms", bestScore, duration);
	}

}

