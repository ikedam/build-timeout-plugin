/*
 * The MIT License
 * 
 * Copyright (c) 2014 IKEDA Yasuyuki
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package hudson.plugins.build_timeout.impl;

import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.Run;
import hudson.plugins.build_timeout.BuildTimeOutStrategy;
import hudson.plugins.build_timeout.BuildTimeOutStrategyDescriptor;
import hudson.plugins.build_timeout.BuildTimeoutWrapper;

/**
 * Timeout when specified time passed since the last output.
 */
public class NoActivityTimeOutStrategy extends BuildTimeOutStrategy {
    private final long timeout;
    
    /**
     * @return
     */
    public long getTimeoutSeconds() {
        return timeout / 1000L;
    }
    
    /**
     * @param timeoutSeconds
     */
    @DataBoundConstructor
    public NoActivityTimeOutStrategy(long timeoutSeconds) {
        this.timeout = timeoutSeconds * 1000L;
    }
    
    /**
     * @param run
     * @return
     * @see hudson.plugins.build_timeout.BuildTimeOutStrategy#getTimeOut(hudson.model.Run)
     */
    @Override
    public long getTimeOut(@SuppressWarnings("rawtypes") Run run) {
        return timeout;
    }
    
    /**
     * @param build
     * @param b
     * @see hudson.plugins.build_timeout.BuildTimeOutStrategy#onWrite(int)
     */
    @Override
    public void onWrite(AbstractBuild<?,?> build, int b) {
        if (b != '\r' && b != '\n') {
            // process only when it is a line break.
            return;
        }
        BuildTimeoutWrapper.EnvironmentImpl env = build.getEnvironments().get(BuildTimeoutWrapper.EnvironmentImpl.class);
        if (env != null) {
            env.rescheduleIfScheduled();
        }
    }
    
    @Extension
    public static class DescriptorImpl extends BuildTimeOutStrategyDescriptor {
        @Override
        public String getDisplayName() {
            return Messages.NoActivityTimeOutStrategy_DisplayName();
        }
    }
}
