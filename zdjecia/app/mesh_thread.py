from subprocess import Popen, PIPE, call, run
import subprocess
from threading import Thread


class MeshThread(Thread):

    def __init__(self, meshroom, input, output, socketio):
        super(MeshThread, self).__init__()
        self.meshroom = meshroom
        self.input = input
        self.output = output
        self.socketio = socketio
        self.success = False

    def run(self):
        proc = Popen([self.meshroom,
                      '--input',
                     self.input,
                      '--output',
                      self.output],
                     stdout=PIPE)
        self.socketio.emit('my event', {"message": "Working..."})
        while proc.returncode is None:
            out = proc.stdout.readline()
            if len(out) > 0:
                #print(out)

                if b'[12/12] Publish' in out or \
                        b'Nodes to execute:  []\n' in out:
                    self.success = True
                    break
                elif b'Aborted' in out:
                    self.success = False
                    break

        self.socketio.emit('my event', {"message": "Done!"})
