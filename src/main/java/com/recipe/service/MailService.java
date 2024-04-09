package com.recipe.service;

import java.io.UnsupportedEncodingException;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MailService {
	
	 private final JavaMailSender javaMailSender; //메일을 발송 java메일 api를 이용
	    private static final String senderEmail= "eenql0529@gmail.com"; //발신자메일
	    private static final String senderName = "이젠 해먹자"; //이젠해먹자 <iptime102030@gmail.com>
	    private static int number;

	    public static void createNumber(){
	        number = (int)(Math.random() * (90000)) + 100000;// (int) Math.random() * (최댓값-최소값+1) + 최소값
	    }

	    public MimeMessage CreateMail(String email){
	        createNumber(); //랜덤으로 난수 설정
	        MimeMessage message = javaMailSender.createMimeMessage();

	        try {
	        	try {
					message.setFrom(new InternetAddress(senderEmail, senderName));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
	            
	            System.out.println("Sending email to: " + email);
	            
	            message.setRecipients(MimeMessage.RecipientType.TO, email);
	            message.setSubject("이메일 인증");
	            String body = "";
	            body += "<h3>" + "요청하신 인증 번호입니다." + "</h3>";
	            body += "<h1>" + number + "</h1>";
	            body += "<h3>" + "감사합니다." + "</h3>";
	            message.setText(body,"UTF-8", "html");
	        } catch (MessagingException e) {
	            e.printStackTrace();
	        }

	        return message;
	    }
	    
	    //메일 최종전달
	    public int sendMail(String email){
	        MimeMessage message = CreateMail(email);
	        javaMailSender.send(message);

	        return number;
	    }
}