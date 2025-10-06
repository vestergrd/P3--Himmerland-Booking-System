package com.auu_sw3_6.Himmerland_booking_software.service;

import java.time.LocalDate;

import com.mailgun.api.v3.MailgunMessagesApi;
import com.mailgun.model.message.Message;
import com.mailgun.model.message.MessageResponse;
import com.mailgun.client.MailgunClient;

import java.io.File;

public class EmailService {
	private final static String API_KEY = "Super-secret-Api-key";
	private static final String EMAIL_FROM = "no-reply@hbs.jonasp.dk";
	private final static String DOMAIN = "hbs.jonasp.dk";

	private final static MailgunMessagesApi mailgunMessagesApi = MailgunClient
			.config("https://api.eu.mailgun.net", API_KEY)
			.createApi(MailgunMessagesApi.class);


	private static void sendEmail(Message message) {
		try {
			// MessageResponse messageResponse = mailgunMessagesApi.sendMessage(DOMAIN, message);
			// System.out.println("Email sent successfully: " + messageResponse.getMessage());
			System.out.println("Email would have been sent successfully, probably..., but who knows without a valid API key");
		} catch (Exception e) {
			System.err.println("Error sending email: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public static void sendPickupNotification(String reciever, String user, String resourceName, String timeRange) {
		System.out.println("Sending pickup notification to: " + reciever);

		String content = "Hej <strong>%s</strong>,<br>Du har en afhentning af: <strong>%s</strong> imellem <strong>%s</strong> i dag. Husk at hente det i tide!"
				.formatted(user, resourceName, timeRange);

		Message message = buildMessage(reciever, "Reservation afhentning: ", content);

		sendEmail(message);
	}

	public static void sendDropoffNotification(String reciever, String user, String resourceName, String timeRange) {
		System.out.println("Sending dropoff notification to: " + reciever);

		String content = "Hej <strong>%s</strong>,<br>Du har en aflevering af: <strong>%s</strong> imellem <strong>%s</strong> i dag. Husk at aflevere det i tide!"
				.formatted(user, resourceName, timeRange);

		Message message = buildMessage(reciever, "Reservation aflevering: ", content);

		sendEmail(message);
	}

	public static void sendMissedDropoffNotification(String reciever, String user, String resourceName,
			String timeRange,
			LocalDate dropoffDate) {
		System.out.println("Sending missed dropoff notification to: " + reciever);

		String content = """
				    Hej <strong>%s</strong>,<br>
				    Du havde en aflevering af: <strong>%s</strong> den <strong>%s</strong> imellem <strong>%s</strong>.
				    Du har desværre ikke afleveret det i tide! Kontakt os venligst for at aftale en ny tid for aflevering.
				""".formatted(user, resourceName, dropoffDate, timeRange);

		Message message = buildMessage(reciever, "Manglende aflevering: ", content);

		sendEmail(message);
	}

	public static void sendCancelledBookingNotification(String reciever, String user, String resourceName,
			LocalDate startDate,
			LocalDate endDate) {
		System.out.println("Sending cancelled booking notification to: " + reciever);

		String content = """
				    Hej <strong>%s</strong>,<br>
				    Din reservation af: <strong>%s</strong> imellem <strong>%s</strong> og <strong>%s</strong> er blevet aflyst.
				    Kontakt os venligst for at aftale en ny tid for afhentning eller aflevering.
				""".formatted(user, resourceName, startDate, endDate);

		Message message = buildMessage(reciever, "Reservation aflyst: " + resourceName, content);

		sendEmail(message);
	}

	private static Message buildMessage(String reciever, String subject, String content) {
		String htmlBody = generateHtmlBody(subject, content);
    File imageFile = new File("src/main/resources/himmerlandLogo.jpg");

		return Message.builder()
				.from(EMAIL_FROM)
				.to(reciever)
				.subject(subject)
				.html(htmlBody)
				.attachment(imageFile)
				.inline(imageFile)		
				.build();
	}

	private static String generateHtmlBody(String title, String content) {

		String signature = """
										<hr style="border: none; border-top: 1px solid #ccc; margin: 20px 0;">
										<p>
												Med venlig hilsen,<br>
												<strong>Himmerland Booking Team</strong><br>
            						<img src="cid:himmerlandLogo.jpg" alt="Himmerland Booking" style="width: 150px; display: block; margin-top: 10px;"><br>
												<a href="mailto:support@himmerland.dk" style="color: #007BFF; text-decoration: none;">support@himmerland.dk</a><br>
												Tlf: +45 123 456 78
										</p>
								"""
				.formatted();

		return """
						<html>
						<body style="font-family: Arial, sans-serif; color: #333; line-height: 1.6;">
								<h2 style="color: #007BFF;">%s</h2>
								<p>%s</p>
								%s
						</body>
						</html>
				""".formatted(title, content, signature);
	}

}