package org.vai.com.processor;

import android.os.Bundle;

public interface Processor {

	public void execute(ProcessorCallback callback, Bundle extras);

}