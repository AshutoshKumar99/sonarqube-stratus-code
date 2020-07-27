package com.pb.stratus.controller.service;

public interface BaseMapImageService {

	public byte[] getMap(RenderNamedMapParams params) throws BaseMapImageServiceException;
}
