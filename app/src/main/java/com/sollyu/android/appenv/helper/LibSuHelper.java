package com.sollyu.android.appenv.helper;

import eu.chainfire.libsuperuser.Shell;

/**
 * 作者: Sollyu
 * 时间: 16/9/19
 * 联系: sollyu@qq.com
 * 说明:
 * <p>
 * compile 'eu.chainfire:libsuperuser:1.0.0.+'
 * <p>
 * 帮助类
 */
public class LibSuHelper {

    private Shell.Interactive rootSession = null;

    private static final LibSuHelper instance = new LibSuHelper();

    public static LibSuHelper getInstance() {
        return instance;
    }

    public Shell.Interactive getRootSession() {
        return rootSession;
    }

    /**
     * <p>
     * Add a command to execute, with a callback to be called on completion
     * </p>
     * <p>
     * The thread on which the callback executes is dependent on various
     * factors, see {@link Shell.Interactive} for further details
     * </p>
     *
     * @param command                 Command to execute
     * @param code                    User-defined value passed back to the callback
     * @param onCommandResultListener Callback to be called on completion
     */
    public void addCommand(String command, int code, Shell.OnCommandResultListener onCommandResultListener) {
        if (rootSession == null || !rootSession.isRunning()) {
            rootSession = new Shell.Builder().useSU().setWantSTDERR(true).setWatchdogTimeout(30000).setMinimalLogging(true).open();
        }

        if ((rootSession == null || !rootSession.isRunning()) && onCommandResultListener != null) {
            onCommandResultListener.onCommandResult(-1, -1, null);
        }

        rootSession.addCommand(command, code, onCommandResultListener);
    }

    public interface OnCommandResultListener {
        void onSuccess(int exitCode);

        void onFailure(int exitCode);
    }

}

