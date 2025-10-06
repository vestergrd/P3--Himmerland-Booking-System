const { spawn } = require('child_process');
const path = require('path');

// Use the correct path for the backend directory
const backendDir = path.join(__dirname, 'backend');
const isWindows = process.platform === 'win32';
const command = isWindows ? 'gradlew.bat' : './gradlew';
const args = ['bootRun'];

// Execute the command in the backend directory
const backendProcess = spawn(command, args, { cwd: backendDir, shell: true });

backendProcess.stdout.on('data', (data) => {
  console.log(`stdout: ${data}`);
});

backendProcess.stderr.on('data', (data) => {
  console.error(`stderr: ${data}`);
});

backendProcess.on('error', (error) => {
  console.error(`Error: ${error.message}`);
});

backendProcess.on('close', (code) => {
  console.log(`Process exited with code ${code}`);
});
