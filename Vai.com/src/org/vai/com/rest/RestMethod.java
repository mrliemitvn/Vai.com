package org.vai.com.rest;

import org.vai.com.resource.Resource;

public interface RestMethod<T extends Resource> {

	public RestMethodResult<T> execute();
}
