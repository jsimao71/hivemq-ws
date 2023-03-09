/**
 * 
 */
package com.hivemq.ws.web;

import java.io.IOException;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.util.StreamUtils;

/**
 *
 */
public class OctetStreamHttpMessageConverter extends AbstractHttpMessageConverter<byte[]> {

	/**
	 * Create a new instance of the {@code OctetHttpMessageConverter}.
	 */
	public OctetStreamHttpMessageConverter() {
		super(MediaType.APPLICATION_OCTET_STREAM);
	}


	@Override
	public boolean supports(Class<?> clazz) {
		return byte[].class == clazz;
	}

	@Override
	public byte[] readInternal(Class<? extends byte[]> clazz, HttpInputMessage message) throws IOException {
		long length = message.getHeaders().getContentLength();
		return (length >= 0 && length < Integer.MAX_VALUE ?
				message.getBody().readNBytes((int) length) : message.getBody().readAllBytes());
	}

	@Override
	protected Long getContentLength(byte[] bytes, @Nullable MediaType contentType) {
		return (long) bytes.length;
	}

	@Override
	protected void writeInternal(byte[] bytes, HttpOutputMessage outputMessage) throws IOException {
		StreamUtils.copy(bytes, outputMessage.getBody());
	}

}
