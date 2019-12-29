from subprocess import Popen, PIPE, call, run
import subprocess
from threading import Thread


class MeshThread(Thread):
    def run(self):
        proc = Popen(['/home/grobocop/meshroom/Meshroom-2019.2.0/meshroom_photogrammetry',
                      '--input',
                      '/home/grobocop/dataset_monstree/full',
                      '--output',
                      '/home/grobocop/output'],
                     stdout=PIPE)
        while proc.returncode == None:
            out = proc.stdout.readline()
            if len(out) > 0:
                print(out)
                if b'[12/12] Publish' in out or b'Nodes to execute:  []\n' in out:
                    break

        print('process finished')


if __name__ == '__main__':
    thread = MeshThread()
    thread.start()
    thread.join()
