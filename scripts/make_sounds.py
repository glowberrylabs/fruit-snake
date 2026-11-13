import math
import struct
import wave
from pathlib import Path

SAMPLE_RATE = 44100
OUTPUT_DIR = Path(__file__).resolve().parent.parent / "app" / "src" / "main" / "res" / "raw"


def note(frequency, seconds, amplitude=0.35, harmonic=0.12):
    total = int(SAMPLE_RATE * seconds)
    attack_samples = max(1, int(SAMPLE_RATE * 0.008))
    release_samples = max(1, int(SAMPLE_RATE * 0.05))
    samples = []
    for index in range(total):
        time = index / SAMPLE_RATE
        attack = min(1.0, index / attack_samples)
        release = min(1.0, (total - index) / release_samples)
        envelope = attack * release
        value = amplitude * envelope * (
            math.sin(2 * math.pi * frequency * time)
            + harmonic * math.sin(4 * math.pi * frequency * time)
        )
        samples.append(value)
    return samples


def silence(seconds):
    return [0.0] * int(SAMPLE_RATE * seconds)


def write_wav(path, samples):
    path.parent.mkdir(parents=True, exist_ok=True)
    frames = b"".join(
        struct.pack("<h", int(max(-1.0, min(1.0, sample)) * 32767))
        for sample in samples
    )
    with wave.open(str(path), "w") as wav:
        wav.setnchannels(1)
        wav.setsampwidth(2)
        wav.setframerate(SAMPLE_RATE)
        wav.writeframes(frames)
    print(f"wrote {path} ({len(samples) / SAMPLE_RATE:.2f}s)")


def main():
    eat = note(880, 0.06) + silence(0.01) + note(1320, 0.09, amplitude=0.3)
    game_over = (
        note(392, 0.13, amplitude=0.32)
        + silence(0.015)
        + note(330, 0.13, amplitude=0.3)
        + silence(0.015)
        + note(262, 0.18, amplitude=0.28)
    )
    write_wav(OUTPUT_DIR / "eat.wav", eat)
    write_wav(OUTPUT_DIR / "game_over.wav", game_over)


if __name__ == "__main__":
    main()
