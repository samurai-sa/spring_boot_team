package com.example.eventapp.services;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Hashtable;
import java.util.Optional;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.eventapp.entities.Event;
import com.example.eventapp.entities.EventTicket;
import com.example.eventapp.repositories.EventRepository;
import com.example.eventapp.repositories.EventTicketRepository;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Binarizer;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.EncodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

@Service
public class EventTicketService {

	private final EventTicketRepository eventTicketRepo;
	private final EventRepository eventRepo;

	public EventTicketService(EventTicketRepository eventTicketRepo, EventRepository eventRepo) {
		this.eventTicketRepo = eventTicketRepo;
		this.eventRepo = eventRepo;
	}

	@Transactional(readOnly = true)
	public EventTicket findById(long id) {
		return eventTicketRepo.findById(id).orElseThrow();
	}

	public String generateTicketNum(Long eventId) {
		// ランダムな番号を生成
		String randomNum = RandomStringUtils.randomNumeric(10);
		String ticketNum = eventId + "-" + randomNum;
		return ticketNum;
	}

	// 登録
	@Transactional
	public EventTicket register(Long eventId, String ticketNum) {
		Event event = eventRepo.findById(eventId).orElseThrow();
		EventTicket eventTicket = new EventTicket();
		if (event.getMaxTicket() > event.getPrintedTicket()) {
			eventTicket.setEvent(event);
			eventTicket.setTicketNum(ticketNum);
			eventTicketRepo.saveAndFlush(eventTicket);
			event.setPrintedTicket(event.getPrintedTicket() + 1);
			eventRepo.saveAndFlush(event);
		}
		return eventTicket;
	}

	/*
	 * // 整理券情報の登録
	 * 
	 * @Transactional
	 * public EventTicket register(Event event) {
	 * 
	 * // 参加上限（MaxTicket）以下→ インスタンス, 番号の生成ができる
	 * 
	 * EventTicket newEventTicket;
	 * if (event.getMaxTicket() > event.getPrintedTicket()) {
	 * 
	 * EventTicket eventTicket = new EventTicket();
	 * 
	 * // ランダムな文字列(数字)を生成
	 * String randomNum = RandomStringUtils.randomNumeric(10);
	 * 
	 * // イベントID＋ランダムな文字列（10桁）
	 * String ticketNum = event.getId() + "-" + randomNum;
	 * 
	 * // 登録をセット＆保存
	 * eventTicket.setTicketNum(ticketNum);
	 * eventTicket.setEvent(event);
	 * newEventTicket = eventTicketRepo.saveAndFlush(eventTicket);
	 * 
	 * // EventのPrintedTicketに＋1する
	 * event.setPrintedTicket(event.getPrintedTicket() + 1);
	 * 
	 * eventRepo.saveAndFlush(event);
	 * 
	 * } else {
	 * throw new RuntimeException("参加上限に達しました。");
	 * }
	 * return newEventTicket;
	 * }
	 */

	// QRコードの生成
	public byte[] qrCreate(String contents) {
		// (EventTicket eventTicket) {
		// String contents = eventTicket.getTicketNum();

		try {
			// QRコードの大きさ
			int width = 200;
			int height = 200;

			// 補正レベルの設定
			Hashtable hints = new Hashtable();
			hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);

			// QRの生成
			ByteArrayOutputStream qrByte = new ByteArrayOutputStream();
			QRCodeWriter qrWriter = new QRCodeWriter();
			BitMatrix bitMatrix = qrWriter.encode(contents, BarcodeFormat.QR_CODE, width, height, hints);
			MatrixToImageWriter.writeToStream(bitMatrix, "png", qrByte);

			return qrByte.toByteArray();

		} catch (WriterException e) {
			System.out.println("[" + contents + "] をエンコードするときに例外が発生しました。");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	// htmlに画像表示するためのBase64変換
	public String convertByteToBase64(byte[] image) {
		String encodedStr = Base64.getEncoder().encodeToString(image);
		String qrImage = "data:image/png;base64," + encodedStr;
		return qrImage;
	}

	// QRコードの情報を読み込む（URLの取り出し）
	public String qrRead(MultipartFile file) {
		String resultContent;

		try {

			// マルチパートファイルをBufferedImageに入るように型変換
			// MultipartFileをFile形式に変換
			InputStream byteStream = new ByteArrayInputStream(file.getBytes());
			BufferedImage image = ImageIO.read(byteStream);
			LuminanceSource source = new BufferedImageLuminanceSource(image);
			Binarizer binarizer = new HybridBinarizer(source);
			BinaryBitmap bitmap = new BinaryBitmap(binarizer);
			QRCodeReader reader = new QRCodeReader();
			Result result = reader.decode(bitmap);

			// Result型をString型に変換
			resultContent = result.getText();

			return resultContent;

			// QRコードの情報以外を読み込んだ時のエラー処理
		} catch (NotFoundException e) {
			// 番号を手入力するページにリダイレクトしたい
			return "error";
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return "error";
		} catch (ChecksumException | FormatException e) {
			System.out.println(e.getMessage());
			return "error";
		} catch (NullPointerException e) {
			// 画像がnullだったら
			return "nullError";
		}
	}

	// 読み取った整理券番号がEventTicketのDBと一致するか
	// 「QRコード等で読み込まれた整理券番号」を読み込む
	// EventIDを取得
	public String authentication(String eventTicketNum, Long eventID) {
		String authenticationResult = "";

		// 整理券番号Repositoryの中に読み込まれた整理券番号があるかどうか
		Optional<EventTicket> eventTicket = eventTicketRepo.findByTicketNum(eventTicketNum);
		// Yes →
		if (eventTicket.isPresent()) {
			// イベントチケットのイベントIDを取得
			EventTicket et = eventTicket.orElseThrow();
			Long eventIdOfTicket = et.getEvent().getId();

			// 渡されたイベントIDと比較 (条件分岐:一致しているかどうか)
			// Yes →
			if (eventIdOfTicket == eventID) {
				// EventTicket（Entitiy）のauthenticatedを読み込む
				Boolean authenticatedOfTicket = et.getAuthenticated();
				// 条件分岐
				// true → retrun "認証済みです"
				if (authenticatedOfTicket) {
					authenticationResult = "認証済みです";
				} else {
					// 認証済みをset()
					et.setAuthenticated(true);

					// 認証日時をset()する
					LocalDateTime localDateTime = LocalDateTime.now();
					et.setAuthenticatedTime(localDateTime);

					// repositoryにEventTicketインスタンスをsaveする
					eventTicketRepo.saveAndFlush(et);

					// return "認証できました"
					authenticationResult = "認証できました";
				}
				// false → tureを登録する

			} else {
				// No → "イベントが違います"(return でメッセージを返す)
				authenticationResult = "イベントが違います";

			}

		}
		// No → "不正な番号です"(returnはStringでメッセージを返す)
		else {
			authenticationResult = "不正な番号です";
		}
		return authenticationResult;

	}

	@Transactional
	public void delete(Long eventTicketId) {
		EventTicket eventTicket = eventTicketRepo.findById(eventTicketId).orElseThrow();
		Event event = eventTicket.getEvent();
		event.setPrintedTicket(event.getPrintedTicket() - 1);
		eventRepo.saveAndFlush(event);
		eventTicketRepo.delete(eventTicket);
	}

}
