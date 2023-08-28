package org.yasya;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import org.yasya.Syllabus.Row;

public class SyllabusPNG {

	synchronized public static BufferedImage getAreaImage(int imgWidth, int imgHeight, Syllabus.Row[][][] rows) {
		int roomsCount = rows[0][0].length;
		int lineHeight = (imgHeight - 40) / (2 * roomsCount - 1);
		int shiftY = (imgHeight - lineHeight * (2 * roomsCount - 1)) / 2;

		int lessonsCount = Constants.SYLLABUS_MAX_LESSONS_COUNT * Constants.SYLLABUS_DAYS.length;

		int part = (imgWidth - 40) / lessonsCount;
		int shiftX = (imgWidth - part * lessonsCount) / 2;

		BufferedImage image = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics graphics = image.getGraphics();
		graphics.setColor(Color.GRAY);
		graphics.fillRect(0, 0, imgWidth, imgHeight);

		for(int day = 0; day < Constants.SYLLABUS_DAYS.length; day++){
			for(int time = 0; time < Constants.SYLLABUS_MAX_LESSONS_COUNT; time++){
				Map<String, Integer> groups = new HashMap<>();
				Map<String, Integer> teachers = new HashMap<>();
				for(int room = 0; room < Constants.SYLLABUS_ROOMS.length; room++){
					Row row = rows[day][time][room];
					if(row == null) continue;
					if(!groups.containsKey(row.group())){
						groups.put(row.group(), 0);
					}
					groups.put(row.group(), groups.get(row.group()) + 1);

					if(!teachers.containsKey(row.teacher())){
						teachers.put(row.teacher(), 0);
					}
					teachers.put(row.teacher(), teachers.get(row.teacher()) + 1);
				}
				for (Integer value : groups.values()){

				}
				for (Integer value : teachers.values()){

				}
			}
		}

		for(int day = 0; day < Constants.SYLLABUS_DAYS.length; day++) {
			for(int time = 0; time < Constants.SYLLABUS_MAX_LESSONS_COUNT; time++) {
				Map<String, Integer> groups = new HashMap<>();
				Map<String, Integer> teachers = new HashMap<>();
				for(int room = 0; room < Constants.SYLLABUS_ROOMS.length; room++){
					Row row = rows[day][time][room];
					if(row == null) continue;
					if(!groups.containsKey(row.group())){
						groups.put(row.group(), 0);
					}
					groups.put(row.group(), groups.get(row.group()) + 1);

					if(!teachers.containsKey(row.teacher())){
						teachers.put(row.teacher(), 0);
					}
					teachers.put(row.teacher(), teachers.get(row.teacher()) + 1);
				}
				for(int room = 0; room < Constants.SYLLABUS_ROOMS.length; room++) {
					if(rows[day][time][room] == null) {
						graphics.setColor(Color.WHITE);
					}
					else if(groups.get(rows[day][time][room].group()) > 1 && teachers.get(rows[day][time][room].teacher()) > 1){
						graphics.setColor(Color.BLACK);
					}
					else if(groups.get(rows[day][time][room].group()) > 1){
						graphics.setColor(Color.RED);
					}
					else if(teachers.get(rows[day][time][room].teacher()) > 1){
						graphics.setColor(Color.YELLOW);
					}
					else{
						graphics.setColor(Color.GREEN);
					}
					graphics.fillRect(
						shiftX + (day * Constants.SYLLABUS_MAX_LESSONS_COUNT + time) * part,
						shiftY + room * 2 * lineHeight,
						part,
						lineHeight
					);
				}
			}
		}

		graphics.dispose();

		return image;
	}
}

