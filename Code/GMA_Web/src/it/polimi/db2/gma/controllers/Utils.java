package it.polimi.db2.gma.controllers;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class Utils {

	public static <E extends Enum<E>> boolean isInEnum(String value, Class<E> enumClass) {
		for (E e : enumClass.getEnumConstants()) {
			if (e.name().equals(value)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isValidDate(String inDate) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		dateFormat.setLenient(false);
		try {
			dateFormat.parse(inDate.trim());
		} catch (ParseException pe) {
			return false;
		}
		return true;
	}

	public static Date dateFromString(String inDate) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		dateFormat.setLenient(false);
		try {
			return dateFormat.parse(inDate.trim());
		} catch (ParseException pe) {
			return null;
		}
	}

	public static boolean isDateGraterThanYesterday(Date inDate) {
		LocalDate today = LocalDate.now();
		LocalDate yesterday = today.minusDays(1);
		LocalDate date = inDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		return date.isAfter(yesterday);
	}

	public static boolean isDateSmallerThanToday(Date inDate) {
		LocalDate today = LocalDate.now();
		LocalDate date = inDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		return date.isBefore(today);
	}

	public static byte[] readImage(InputStream imageInputStream) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[4096];// image can be maximum of 4MB
		int bytesRead = -1;

		try {
			while ((bytesRead = imageInputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
			}

			byte[] imageBytes = outputStream.toByteArray();
			return imageBytes;
		} catch (IOException e) {
			throw e;
		}
	}

	public static void processError(WebContext context, TemplateEngine templateEngine, String errorMsg) {
		String path = "/WEB-INF/Error.html";
		context.setVariable("error", errorMsg);
		try {
			templateEngine.process(path, context, context.getResponse().getWriter());
		} catch (IOException e) {
		}
	}

}
