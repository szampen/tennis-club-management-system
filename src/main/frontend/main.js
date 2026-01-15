const { app, BrowserWindow } = require('electron');
const { spawn } = require('child_process');
const path = require('path');

let serverProcess;

function createWindow() {
    const win = new BrowserWindow({
        width: 1400,
        height: 800,
        show: false,
        webPreferences: {
            nodeIntegration: false,
            contextIsolation: true,
            webSecurity: true // Helps with loading resources from localhost
        }
    });

    win.once('ready-to-show', () => {
        win.show();
    });

    if (app.isPackaged) {
        win.loadFile(path.join(__dirname, 'dist', 'index.html')).catch(err => {
            console.error("Error loading file:", err);
        });
    } else {
        win.loadURL('http://localhost:5173');
    }

    win.webContents.on('did-fail-load', () => {
        setTimeout(() => {
            if (!win.isDestroyed()) win.reload();
        }, 1000);
    });
}

app.whenReady().then(() => {
    const javaPath = app.isPackaged
        ? path.join(process.resourcesPath, 'jre', 'bin', 'java.exe')
        : 'java';

    const jarPath = app.isPackaged
        ? path.join(process.resourcesPath, 'backend.jar')
        : path.join(__dirname, '../../../target/backend.jar');


    serverProcess = spawn(javaPath, ['-jar', jarPath], {
        cwd: app.isPackaged ? process.resourcesPath : undefined,
        shell: false,
        windowsHide: true // Hides black CMD in the back
    });

    createWindow();
});

app.on('window-all-closed', () => {
    if (serverProcess) {
        // Killing SpringBoot process on Windows
        spawn("taskkill", ["/pid", serverProcess.pid, '/f', '/t']);
    }
    if (process.platform !== 'darwin') app.quit();
});