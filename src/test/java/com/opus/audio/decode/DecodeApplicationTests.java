package com.opus.audio.decode;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@SpringBootTest
class DecodeApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	public void shouldGetEncodedAudio() throws IOException {
		int audioId = 148;
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(Objects.requireNonNull(classLoader.getResource("Download.ogg")).getFile());
		byte[] bytes = getByteArrayFromFile(file);

	}

	private static byte[] getByteArrayFromFile(final File file) throws IOException {
		try (FileInputStream fis = new FileInputStream(file)) {
			final ByteArrayOutputStream bos = new ByteArrayOutputStream((int) file.length());
			final byte[] buffer = new byte[1024];
			int cnt;
			while ((cnt = fis.read(buffer)) != -1) {
				bos.write(buffer, 0, cnt);
			}
			return bos.toByteArray();
		}
	}

}
