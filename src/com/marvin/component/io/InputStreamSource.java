/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.marvin.component.io;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author cdi305
 */
public interface InputStreamSource {
    
    InputStream getInputStream() throws IOException;
    
}