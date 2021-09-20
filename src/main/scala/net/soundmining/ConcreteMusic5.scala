package net.soundmining

import net.soundmining.modular.ModularSynth.relativeThreeBlockcontrol
import net.soundmining.synth.{Instrument, SoundPlay, SoundPlays, SuperColliderClient}
import net.soundmining.synth.SuperColliderClient.{allocRead, loadDir}

object ConcreteMusic5 {
  val KNIFE_SOUNDS_DIR = "/Users/danielstahl/Documents/Music/sounds/Knife Sounds_rendered/"
  val WATER_SOUNDS_DIR = "/Users/danielstahl/Documents/Music/sounds/Water Sounds_rendered/"

  val sounds = Map(
    "knife-1" -> SoundPlay(0, 0.184, 1.153),
    "knife-2" -> SoundPlay(1, 0.133, 1.424),
    "knife-3" -> SoundPlay(2, 0.282, 2.00),
    "knife-4" -> SoundPlay(3, 0.256, 1.75),
    "knife-5" -> SoundPlay(4, 0.278, 1.638),
    "knife-6" -> SoundPlay(5, 0.171, 1.307, peakTimes = Seq(0.505, 1.018)),

    // 1.178, 2.563, 4.936, 6.776, 8.650
    "water-1" -> SoundPlay(10, 0.091, 7.058, amp = amp => relativeThreeBlockcontrol(0, 0.001, amp, amp, 0.1, 0, Right(Instrument.SINE)), peakTimes = Seq(1.178, 2.563, 4.936, 6.776, 8.650)),
    "water-2" -> SoundPlay(11, 0.0, 5.50),
    "water-3" -> SoundPlay(12, 0.0, 17.50, peakTimes = Seq(1.951, 3.303, 3.813, 7.308, 9.604, 10.306, 15.293, 16.543)),
    "water-4" -> SoundPlay(13, start = 0.345, end = 23.50),
    "water-5" -> SoundPlay(14, 0.587, 2.50),
    "water-6" -> SoundPlay(15, 0.183, 3.276, peakTimes = Seq(0.522, 0.933, 1.657, 1.991))

  )

  implicit val client: SuperColliderClient = SuperColliderClient()
  val SYNTH_DIR = "/Users/danielstahl/Documents/Projects/soundmining-modular/src/main/sc/synths"

  val soundPlays = SoundPlays(sounds, masterVolume = 1, numberOfOutputBuses = 64)

  import soundPlays._

  // Convolution reverb
  // https://www.youtube.com/watch?v=Dc6cwZFuO0U

  // ConcreteMusic5.testMix(sound1 = "water-6", sound1Start = 0, sound1Pan = (0.1, -0.5), sound2 = "water-5", sound2Start = 0.7, sound2Pan = (-0.1, 0.5))
  // ConcreteMusic5.testMix(sound1 = "knife-2", sound1Start = 0, sound1Pan = (-0.3, -0.9), sound2 = "knife-3", sound2Start = 0.9, sound2Pan = (0.3, 0.9))
  // The below can "resolve" into water 3
  // ConcreteMusic5.testMix(sound1 = "knife-6", sound1Start = 0, sound1Pan = (-0.3, -0.9), sound2 = "water-6", sound2Start = 0.4, sound2Pan = (0.3, 0.9))
  def testMix(sound1: String, sound1Start: Double = 0.0, sound1volume: Double = 1.0, sound1Pan: (Double, Double) = (0.0, 0.0),
              sound2: String, sound2Start: Double = 0.0, sound2volume: Double = 1.0, sound2Pan: (Double, Double) = (0.0, 0.0)): Unit = {
    client.resetClock
    playSound2(sound1, sound1Start, volume = sound1volume, rate = 1.0, pan = sound1Pan)
    playSound2(sound2, sound2Start, volume = sound2volume, rate = 1.0, pan = sound2Pan)

  }


  // The below can "resolve" into water 3
  // ConcreteMusic5.testMix(sound1 = "knife-6", sound1Start = 0, sound1Pan = (-0.3, -0.9), sound2 = "water-6", sound2Start = 0.4, sound2Pan = (0.3, 0.9))
  // Try to make echoes. E.g the same two sounds but with different rate and/or high, low pass.
  def testThree(start: Double = 0): Unit = {
    client.resetClock
    playSound2("knife-6", 0, volume = 1.0, rate = 1.0, pan = (-0.3, -0.9))
    playSound2("water-6", 0.4, volume = 1.0, rate = 1.0, pan = (0.3, 0.9))
    playSound2("water-3", 2, volume = 1.0, rate = 1.0, pan = (-0.5, 0.2))
    playSound2("water-5", 11, volume = 1.0, rate = 1.0, pan = (-0.2, 0.2))
    playSound2("knife-3", 17, volume = 1.0, rate = 1.0, pan = (0.1, -0.3))
  }

  /*
  https://depts.washington.edu/dxscdoc/Help/Classes/WaveLoss.html
  Maybe we could use WaveLoss to add some distortion to the sounds. It
  will drop fractions of the sound and replace it with silence.
  * */

/*
Water that crossfade between highpass and lowpass.
* */

  object ConcreteMusic5 {

    val melody = Seq(0, 1, 3, 2, 4)

    val melodyDelayed = Seq(3, 2, 4, 0, 1)

    /*
    Find out the themes for different parameters
    * Relative time. Have maybe three or four times and build themes from that
    * Filter freq. Have six relative freq and build themes for that
    * make them concrete with, for instance, high, low and middle. Take
    * a range of freq and divide it into 6 steps. E.g from 1000 to 5000
    *
    * Retrograde backward.
    * Inversion mirror.
    * */

    def exposition(start: Double = 0): Unit = {
      client.resetClock
      val highScale = Melody.absolute(2000, List.fill(7)((9000 - 2000) / 6))
      val lowScale = Melody.absolute(200, List.fill(7)((400 - 200) / 6))

      val times = Melody.absolute(start, Seq(
        sounds("water-3").absoluteTime(10.306, 1.0),
        sounds("water-3").absoluteTime(10.306, 0.99),
        sounds("water-3").absoluteTime(7.308, 1.01),
        sounds("water-3").absoluteTime(10.306, 0.99),
        sounds("water-3").absoluteTime(15.293, 1),

        sounds("water-1").absoluteTime(4.936, 1.0),
        sounds("water-1").absoluteTime(4.936, 0.99),
        sounds("water-1").absoluteTime(2.563, 1.01),
        sounds("water-1").absoluteTime(4.936, 0.99),
        sounds("water-1").absoluteTime(6.776, 1),

        sounds("water-3").absoluteTime(10.306, 1.0),
        sounds("water-3").absoluteTime(10.306, 0.99),
        sounds("water-3").absoluteTime(15.293, 1.01),

        sounds("water-1").absoluteTime(4.936, 0.99),
        sounds("water-1").absoluteTime(6.776, 1)))

      playSound2("water-3", times.head, volume = 1.0, rate = 1.0, pan = (-0.5, 0.2), highPass = Some(highScale(melody.head)), outputBus = 0)
      playSound2("water-3", times.head, volume = 3, rate = 1.0, pan = (-0.4, 0.1), bandPass = Some((lowScale(melodyDelayed.head), 0.05)), outputBus = 2)

      playSound2("knife-4", times.head + 1.951, volume = 1, pan = (-0.1, 0.4), highPass = Some(highScale(melody.head)), outputBus = 8)
      playSound2("knife-4", times.head + 1.951, volume = 3, pan = (-0.3, 0.3), bandPass = Some(lowScale(melodyDelayed.head), 0.05), outputBus = 10)

      playSound2("water-3", times(1), volume = 1.0, rate = 0.99, pan = (0.5, -0.2), highPass = Some(highScale(melody(1))), outputBus = 0)
      playSound2("water-3", times(1), volume = 3, rate = 0.99, pan = (0.4, -0.1), bandPass = Some((lowScale(melodyDelayed(1)), 0.05)), outputBus = 2)

      playSound2("water-3", times(2), volume = 1.0, rate = 1.01, pan = (-0.5, 0.2), highPass = Some(highScale(melody(2))), outputBus = 0)
      playSound2("water-3", times(2), volume = 3, rate = 1.01, pan = (-0.4, 0.1), bandPass = Some((lowScale(melodyDelayed(2)), 0.05)), outputBus = 2)

      playSound2("water-3", times(3), volume = 1.0, rate = 0.99, pan = (0.5, -0.2), highPass = Some(highScale(melody(3))), outputBus = 0)
      playSound2("water-3", times(3), volume = 3, rate = 0.99, pan = (0.4, -0.1), bandPass = Some((lowScale(melodyDelayed(3)), 0.05)), outputBus = 2)

      playSound2("water-3", times(4), volume = 1.0, rate = 1.0, pan = (-0.5, 0.2), highPass = Some(highScale(melody(4))), outputBus = 0)
      playSound2("water-3", times(4), volume = 3, rate = 1.0, pan = (-0.4, 0.1), bandPass = Some((lowScale(melodyDelayed(4)), 0.05)), outputBus = 2)


      playSound2("water-1", times(5), volume = 1, rate = 1.0, pan = (0.6, -0.4),  highPass = Some(highScale(melody.head)), outputBus = 4)
      playSound2("water-1", times(5), volume = 3, rate = 1.0, pan = (0.5, -0.2), bandPass = Some((lowScale(melodyDelayed.head), 0.05)), outputBus = 6)

      playSound2("knife-3", times(5) + 1.178, volume = 1, pan = (0.4, -0.2), highPass = Some(highScale(melody(1))), outputBus = 8)
      playSound2("knife-3", times(5) + 1.178, volume = 3, pan = (0.3, -0.3), bandPass = Some((lowScale(melodyDelayed(1)), 0.05)), outputBus = 10)

      playSound2("water-1", times(6), volume = 1, rate = 0.99, pan = (-0.6, 0.4),  highPass = Some(highScale(melody(1))), outputBus = 4)
      playSound2("water-1", times(6), volume = 2, rate = 0.99, pan = (-0.5, 0.2), bandPass = Some((lowScale(melodyDelayed(1)), 0.05)), outputBus = 6)

      playSound2("water-1", times(7), volume = 1, rate = 1.01, pan = (0.6, -0.4),  highPass = Some(highScale(melody(2))), outputBus = 4)
      playSound2("water-1", times(7), volume = 2, rate = 1.01, pan = (0.5, -0.2), bandPass = Some((lowScale(melodyDelayed(2)), 0.05)), outputBus = 6)

      playSound2("water-1", times(8), volume = 1, rate = 0.99, pan = (-0.6, 0.4),  highPass = Some(highScale(melody(3))), outputBus = 4)
      playSound2("water-1", times(8), volume = 2, rate = 0.99, pan = (-0.5, 0.2), bandPass = Some((lowScale(melodyDelayed(3)), 0.05)), outputBus = 6)

      playSound2("water-1", times(9), volume = 1, rate = 1.0, pan = (0.6, -0.4),  highPass = Some(highScale(melody(4))), outputBus = 4)
      playSound2("water-1", times(9), volume = 2, rate = 1.0, pan = (0.5, -0.2), bandPass = Some((lowScale(melodyDelayed(4)), 0.05)), outputBus = 6)


      playSound2("water-3", times(10), volume = 1.0, rate = 1.0, pan = (-0.5, 0.2), highPass = Some(highScale(melody.head)), outputBus = 0)
      playSound2("water-3", times(10), volume = 3, rate = 1.0, pan = (-0.4, 0.1), bandPass = Some((lowScale(melodyDelayed.head), 0.05)), outputBus = 2)

      playSound2("knife-4", times(10) + 1.951, volume = 1, pan = (-0.1, 0.4), highPass = Some(highScale(melody(2))), outputBus = 8)
      playSound2("knife-4", times(10) + 1.951, volume = 2, pan = (-0.3, 0.3), bandPass = Some(lowScale(melodyDelayed(2)), 0.05), outputBus = 10)

      playSound2("water-3", times(11), volume = 1.0, rate = 0.99, pan = (0.5, -0.2), highPass = Some(highScale(melody(1))), outputBus = 0)
      playSound2("water-3", times(11), volume = 3, rate = 0.99, pan = (0.4, -0.1), bandPass = Some((lowScale(melodyDelayed(1)), 0.05)), outputBus = 2)

      playSound2("water-3", times(12), volume = 1.0, rate = 1.01, pan = (-0.5, 0.2), highPass = Some(highScale(melody(2))), outputBus = 0)
      playSound2("water-3", times(12), volume = 3, rate = 1.01, pan = (-0.4, 0.1), bandPass = Some((lowScale(melodyDelayed(2)), 0.05)), outputBus = 2)


      playSound2("water-1", times(13), volume = 1, rate = 1.0, pan = (0.6, -0.4),  highPass = Some(highScale(melody(3))), outputBus = 4)
      playSound2("water-1", times(13), volume = 2, rate = 1.0, pan = (0.5, -0.2), bandPass = Some((lowScale(melodyDelayed(3)), 0.05)), outputBus = 6)

      playSound2("knife-3", times(13) + 1.178, volume = 1, pan = (0.4, -0.2), highPass = Some(highScale(melody(3))), outputBus = 8)
      playSound2("knife-3", times(13) + 1.178, volume = 3, pan = (0.3, -0.3), bandPass = Some((lowScale(melodyDelayed(3)), 0.05)), outputBus = 10)

      playSound2("water-1", times(14), volume = 1, rate = 0.99, pan = (-0.6, 0.4),  highPass = Some(highScale(melody(4))), outputBus = 4)
      playSound2("water-1", times(14), volume = 2, rate = 0.99, pan = (-0.5, 0.2), bandPass = Some((lowScale(melodyDelayed(4)), 0.05)), outputBus = 6)

      playSound2("knife-6", times(14) + 5.7, volume = 1, rate = 1, pan = (-0.3, 0.4), highPass = Some(highScale(melody(4))), outputBus = 8)
      playSound2("knife-6", times(14) + 5.7, volume = 3, rate = 1, pan = (-0.6, 0.7), bandPass = Some((lowScale(melodyDelayed(4)), 0.05)), outputBus = 10)
      // scala
      // 0 1 2 3 4 5 6

      // Theme
      // 0 1 3 2 4

      // Inversion
      // 6 5 3 4 2

      // Retrograde
      // 4 2 3 1 0

      // Retrograde Inversion
      // 2 4 3 5 6


    }

    def development1(start: Double = 0): Unit = {
      client.resetClock

      val middleHighScale = Melody.absolute(2500, List.fill(7)((3000 - 2500) / 6))
      val middleLowScale = Melody.absolute(1500, List.fill(7)((2000 - 1500) / 6))

      val highScale = Melody.absolute(4000, List.fill(7)((5000 - 4000) / 6))
      val lowScale = Melody.absolute(200, List.fill(7)((400 - 200) / 6))

      val times = Melody.absolute(start, Seq(
        sounds("water-3").absoluteTime(10.306, 1.0),
        sounds("water-3").absoluteTime(10.306, 0.99),
        sounds("water-3").absoluteTime(7.308, 1.01),
        sounds("water-3").absoluteTime(10.306, 0.99),
        sounds("water-3").absoluteTime(15.293, 1)))

      val shortWater1 = Melody.absolute(times.head + sounds("water-3").absoluteTime(7.308, 0.99), Seq(
        sounds("water-1").absoluteTime(4.936, 1.0),
        sounds("water-1").absoluteTime(4.936, 0.99),
        sounds("water-1").absoluteTime(2.563, 1.01)))

      val shortWater2 = Melody.absolute(times(3) + sounds("water-3").absoluteTime(7.308, 0.99), Seq(
        sounds("water-1").absoluteTime(2.563, 0.99),
        sounds("water-1").absoluteTime(4.936, 1)))

      playSound2("water-3", times.head, volume = 1, rate = 1.0, pan = (-0.5, 0.2), bandPass = Some((middleHighScale(melody.head), 0.05)), outputBus = 0)
      playSound2("water-3", times.head, volume = 2, rate = 1.0, pan = (-0.8, -0.3), bandPass = Some((middleLowScale(melodyDelayed.head), 0.05)), outputBus = 2)

      playSound2("water-1", shortWater1.head, volume = 1, rate = 1, pan = (0.3, -0.2), bandPass = Some((highScale(melody.head), 0.05)), outputBus = 4)
      playSound2("water-1", shortWater1.head, volume = 1, rate = 1, pan = (0.4, -0.3), bandPass = Some((lowScale(melodyDelayed.head), 0.05)), outputBus = 6)

      playSound2("water-1", shortWater1(1), volume = 1, rate = 0.99, pan = (-0.3, 0.2), bandPass = Some((highScale(melody(1)), 0.05)), outputBus = 4)
      playSound2("water-1", shortWater1(1), volume = 1, rate = 0.99, pan = (-0.4, 0.3), bandPass = Some((lowScale(melodyDelayed(1)), 0.05)), outputBus = 6)

      playSound2("water-1", shortWater1(2), volume = 1, rate = 1.01, pan = (0.3, -0.2), bandPass = Some((highScale(melody(2)), 0.05)), outputBus = 4)
      playSound2("water-1", shortWater1(2), volume = 1, rate = 1.01, pan = (0.4, -0.3), bandPass = Some((lowScale(melodyDelayed(2)), 0.05)), outputBus = 6)


      playSound2("water-3", times(1), volume = 1, rate = 0.99, pan = (0.5, -0.2), bandPass = Some((middleHighScale(melody(1)), 0.05)), outputBus = 0)
      playSound2("water-3", times(1), volume = 2, rate = 0.99, pan = (0.8, -0.3), bandPass = Some((middleLowScale(melodyDelayed(1)), 0.05)), outputBus = 2)

      playSound2("water-3", times(2), volume = 1, rate = 1.01, pan = (-0.5, 0.2), bandPass = Some((middleHighScale(melody(2)), 0.05)), outputBus = 0)
      playSound2("water-3", times(2), volume = 2, rate = 1.01, pan = (-0.8, 0.3), bandPass = Some((middleLowScale(melodyDelayed(2)), 0.05)), outputBus = 2)


      playSound2("water-3", times(3), volume = 1, rate = 0.99, pan = (0.5, -0.2), bandPass = Some((middleHighScale(melody(3)), 0.05)), outputBus = 0)
      playSound2("water-3", times(3), volume = 2, rate = 0.99, pan = (0.8, -0.3), bandPass = Some((middleLowScale(melodyDelayed(3)), 0.05)), outputBus = 2)

      playSound2("water-1", shortWater2.head, volume = 1, rate = 0.99, pan = (-0.3, 0.2), bandPass = Some((highScale(melody(3)), 0.05)), outputBus = 4)
      playSound2("water-1", shortWater2.head, volume = 1, rate = 0.99, pan = (-0.4, 0.3), bandPass = Some((lowScale(melodyDelayed(3)), 0.05)), outputBus = 6)

      playSound2("water-1", shortWater2(1), volume = 1, rate = 1, pan = (0.3, -0.2), bandPass = Some((highScale(melody(4)), 0.05)), outputBus = 4)
      playSound2("water-1", shortWater2(1), volume = 1, rate = 1, pan = (0.4, -0.3), bandPass = Some((lowScale(melodyDelayed(4)), 0.05)), outputBus = 6)


      playSound2("water-3", times(4), volume = 1, rate = 1.0, pan = (-0.5, 0.2), bandPass = Some((middleHighScale(melody(4)), 0.05)), outputBus = 0)
      playSound2("water-3", times(4), volume = 2, rate = 1.0, pan = (-0.8, 0.3), bandPass = Some((middleLowScale(melodyDelayed(4)), 0.05)), outputBus = 2)

      playSound2("knife-4", times(4) + 16.543, volume = 1, pan = (-0.1, 0.4), bandPass = Some((middleHighScale(melody(4)), 0.05)), outputBus = 8)
      playSound2("knife-4", times(4) + 16.543, volume = 1, pan = (-0.3, 0.3), bandPass = Some((middleLowScale(melodyDelayed(4)), 0.05)), outputBus = 10)
    }

    def development2(start: Double = 0): Unit = {
      client.resetClock

      /*
      We could do the short water sound a couple of times that is followed by
      the long sound. End the short sound with knife.
      short -------------------
      knife                 ---
      long            ----------------------

      Start with low sound. Then high. Maybe do more then two
      simultaneous "voices" at the same time.
      * */

      /*
      For melody we could do inversion

      inversion
      6 5 3 4 2

      inversion delay 2
      3 4 2 6 5

      inversion delay 2 delay 2
      2 6 5 3 4

      * */

      // The low
      // 250 550 700

      // The high
      // 3000 4500 9000
      val shortTimes1 = Melody.absolute(start, Seq(
        sounds("water-1").absoluteTime(2.563, 1.0),
        sounds("water-1").absoluteTime(2.563, 0.99),
        sounds("water-1").absoluteTime(2.563, 1.01),
        sounds("water-1").absoluteTime(2.563, 0.99),
        sounds("water-1").absoluteTime(2.563, 1)))

      val longTimes = Melody.absolute(start + shortTimes1(3), Seq(
        sounds("water-3").absoluteTime(7.308, 1.0),
        sounds("water-3").absoluteTime(7.308, 1.01),
        sounds("water-3").absoluteTime(7.308, 1.01)))

      val shortTimes2 = Melody.absolute(start + longTimes(2), Seq(
        sounds("water-1").absoluteTime(2.563, 0.99),
        sounds("water-1").absoluteTime(2.563, 1)))

      val melodyInversion = Seq(6, 5, 3, 4, 2)
      val melodyInversionDelay = Seq(3, 4, 2, 6, 5)
      val melodyInversionDelayDelay = Seq(2, 6, 5, 3, 4)

      val lowScale1 = Melody.absolute(250, List.fill(7)((350 - 250) / 6))
      val lowScale2 = Melody.absolute(550, List.fill(7)((600 - 550) / 6))
      val lowScale3 = Melody.absolute(650, List.fill(7)((750 - 650) / 6))

      val highScale1 = Melody.absolute(3000, List.fill(7)((4000 - 3000) / 6))
      val highScale2 = Melody.absolute(4500, List.fill(7)((6000 - 4500) / 6))
      val highScale3 = Melody.absolute(9000, List.fill(7)((10000 - 9000) / 6))


      playSound2("water-1", shortTimes1.head, volume = 2, rate = 1, pan = (-0.9, -0.5), bandPass = Some((lowScale1(melodyInversion.head), 0.05)), outputBus = 0)
      playSound2("water-1", shortTimes1.head, volume = 2, rate = 1, pan = (-0.7, -0.3), bandPass = Some((lowScale2(melodyInversionDelay.head), 0.05)), outputBus = 2)
      playSound2("water-1", shortTimes1.head, volume = 1, rate = 1, pan = (-0.5, -0.1), bandPass = Some((lowScale3(melodyInversionDelayDelay.head), 0.05)), outputBus = 4)

      playSound2("water-1", shortTimes1(1), volume = 2, rate = 0.99, pan = (0.9, 0.5), bandPass = Some((lowScale1(melodyInversion(1)), 0.05)), outputBus = 0)
      playSound2("water-1", shortTimes1(1), volume = 2, rate = 0.99, pan = (0.7, 0.3), bandPass = Some((lowScale2(melodyInversionDelay(1)), 0.05)), outputBus = 2)
      playSound2("water-1", shortTimes1(1), volume = 1, rate = 0.99, pan = (0.5, 0.1), bandPass = Some((lowScale3(melodyInversionDelayDelay(1)), 0.05)), outputBus = 4)

      playSound2("water-1", shortTimes1(2), volume = 2, rate = 1.01, pan = (-0.9, -0.5), bandPass = Some((lowScale1(melodyInversion(2)), 0.05)), outputBus = 0)
      playSound2("water-1", shortTimes1(2), volume = 2, rate = 1.01, pan = (-0.7, -0.3), bandPass = Some((lowScale2(melodyInversionDelay(2)), 0.05)), outputBus = 2)
      playSound2("water-1", shortTimes1(2), volume = 1, rate = 1.01, pan = (-0.5, -0.1), bandPass = Some((lowScale3(melodyInversionDelayDelay(2)), 0.05)), outputBus = 4)


      playSound2("water-3", longTimes.head, volume = 2, rate = 0.99, pan = (0.9, 0.5), bandPass = Some((highScale1(melodyInversion.head), 0.05)), outputBus = 6)
      playSound2("water-3", longTimes.head, volume = 2, rate = 0.99, pan = (0.7, 0.3), bandPass = Some((highScale2(melodyInversionDelay.head), 0.05)), outputBus = 8)
      playSound2("water-3", longTimes.head, volume = 2, rate = 0.99, pan = (0.5, 0.1), bandPass = Some((highScale3(melodyInversionDelayDelay.head), 0.05)), outputBus = 10)

      playSound2("water-3", longTimes(1), volume = 2, rate = 1, pan = (-0.9, -0.5), bandPass = Some((highScale1(melodyInversion(1)), 0.05)), outputBus = 6)
      playSound2("water-3", longTimes(1), volume = 2, rate = 1, pan = (-0.7, -0.3), bandPass = Some((highScale2(melodyInversionDelay(1)), 0.05)), outputBus = 8)
      playSound2("water-3", longTimes(1), volume = 2, rate = 1, pan = (-0.5, -0.1), bandPass = Some((highScale3(melodyInversionDelayDelay(1)), 0.05)), outputBus = 10)


      playSound2("water-1", shortTimes2.head, volume = 2, rate = 0.99, pan = (0.9, 0.5), bandPass = Some((highScale1(melodyInversion(2)), 0.05)), outputBus = 12)
      playSound2("water-1", shortTimes2.head, volume = 2, rate = 0.99, pan = (0.7, 0.3), bandPass = Some((highScale2(melodyInversionDelay(2)), 0.05)), outputBus = 14)
      playSound2("water-1", shortTimes2.head, volume = 1, rate = 0.99, pan = (0.5, 0.1), bandPass = Some((highScale3(melodyInversionDelayDelay(2)), 0.05)), outputBus = 16)

      playSound2("water-1", shortTimes2(1), volume = 2, rate = 1, pan = (-0.9, -0.5), bandPass = Some((highScale1(melodyInversion(3)), 0.05)), outputBus = 12)
      playSound2("water-1", shortTimes2(1), volume = 2, rate = 1, pan = (-0.7, -0.3), bandPass = Some((highScale2(melodyInversionDelay(3)), 0.05)), outputBus = 14)
      playSound2("water-1", shortTimes2(1), volume = 1, rate = 1, pan = (-0.5, -0.1), bandPass = Some((highScale3(melodyInversionDelayDelay(3)), 0.05)), outputBus = 16)


      playSound2("knife-3", shortTimes2(1) + 1.178, volume = 2, pan = (0.4, -0.2), bandPass = Some((highScale1(melodyInversion(3)), 0.05)), outputBus = 18)
      playSound2("knife-3", shortTimes2(1) + 1.178, volume = 2, pan = (0.3, -0.3), bandPass = Some((highScale2(melodyInversionDelay(3)), 0.05)), outputBus = 20)
      playSound2("knife-3", shortTimes2(1) + 1.178, volume = 2, pan = (0.3, -0.3), bandPass = Some((highScale3(melodyInversionDelayDelay(3)), 0.05)), outputBus = 22)
    }

    def recapitulation(start: Double = 0): Unit = {
      client.resetClock

      val highScale = Melody.absolute(2000, List.fill(7)((9000 - 2000) / 6))
      val lowScale = Melody.absolute(200, List.fill(7)((400 - 200) / 6))

      val times = Melody.absolute(start, Seq(
        sounds("water-3").absoluteTime(10.306, 1.0),
        sounds("water-1").absoluteTime(4.936, 1.0)
      ))

      playSound2("water-3", times.head, volume = 1.0, rate = 1.0, pan = (-0.5, 0.2), highPass = Some(highScale(melody.head)), outputBus = 0)
      playSound2("water-3", times.head, volume = 3, rate = 1.0, pan = (-0.4, 0.1), bandPass = Some((lowScale(melodyDelayed.head), 0.05)), outputBus = 2)

      playSound2("knife-4", times.head + 1.951, volume = 1, pan = (-0.1, 0.4), highPass = Some(highScale(melody.head)), outputBus = 8)
      playSound2("knife-4", times.head + 1.951, volume = 2, pan = (-0.3, 0.3), bandPass = Some(lowScale(melodyDelayed.head), 0.05), outputBus = 10)


      playSound2("water-1", times(1), volume = 1, rate = 0.99, pan = (0.6, -0.4),  highPass = Some(highScale(melody(2))), outputBus = 4)
      playSound2("water-1", times(1), volume = 2, rate = 0.99, pan = (0.5, -0.2), bandPass = Some((lowScale(melodyDelayed(2)), 0.05)), outputBus = 6)

      playSound2("knife-6", times(1) + 5.7, volume = 1, rate = 1, pan = (0.3, -0.4), highPass = Some(highScale(melody(2))), outputBus = 8)
      playSound2("knife-6", times(1) + 5.7, volume = 2, rate = 1, pan = (0.6, -0.7), bandPass = Some((lowScale(melodyDelayed(2)), 0.05)), outputBus = 10)

    }
  }

  object ConcreteMusic6 {
    def exposition61(start: Double = 0): Unit = {
      client.resetClock

      val knifeSound = soundPlays("knife-6")
      val knifeLen = knifeSound.end - knifeSound.start
      val waterSound = soundPlays("water-6")
      val waterLen = waterSound.end - waterSound.start

      val baseLen = waterLen + knifeLen - 0.505

      println(s"knifelen $knifeLen waterlen $waterLen baselen $baseLen")

      val times = Melody.absolute(start, Seq(
        waterLen, waterLen, waterLen, baseLen,
        waterLen, waterLen, waterLen, baseLen,
        waterLen, waterLen, waterLen, waterLen, baseLen,
        waterLen, waterLen, waterLen, baseLen))

      playSound2("knife-6", times.head, volume = 1.0, rate = 1.0, pan = (-0.3, -0.9))
      playSound2("knife-6", times.head, volume = 4.0, rate = 1.0, pan = (-0.4, -0.8), bandPass = Some((100, 0.05)))
      playSound2("water-6", times.head + 0.505, volume = 1.0, rate = 1.0, pan = (0.3, 0.9))
      playSound2("water-6", times.head + 0.505, volume = 3.0, rate = 1.0, pan = (0.4, 0.8), bandPass = Some((200, 0.05)))

      playSound2("knife-6", times(1), volume = 1.0, rate = 1.0, pan = (-0.3, -0.9), highPass = Some(3000))
      playSound2("water-6", times(1) + 0.505, volume = 1.0, rate = 1.0, pan = (0.3, 0.9), highPass = Some(3000))

      playSound2("knife-6", times(2), volume = 1.0, rate = 1.0, pan = (-0.3, -0.9), highPass = Some(6000))
      playSound2("water-6", times(2) + 0.505, volume = 1.0, rate = 1.0, pan = (0.3, 0.9), highPass = Some(6000))

      playSound2("knife-6", times(3), volume = 1.0, rate = 1.0, pan = (-0.3, -0.9), highPass = Some(9000))
      playSound2("water-6", times(3) + 0.505, volume = 1.0, rate = 1.0, pan = (0.3, 0.9), highPass = Some(9000))


      playSound2("knife-6", times(4), volume = 1.0, rate = 1.0, pan = (-0.7, 0.5))
      playSound2("knife-6", times(4), volume = 4.0, rate = 1.0, pan = (-0.8, 0.6), bandPass = Some((100, 0.05)))
      playSound2("water-6", times(4), volume = 1.0, rate = 1.0, pan = (0.7, -0.5))
      playSound2("water-6", times(4), volume = 3.0, rate = 1.0, pan = (0.8, -0.6), bandPass = Some((200, 0.05)))

      playSound2("knife-6", times(5), volume = 3, rate = 1.0, pan = (-0.7, 0.5), bandPass = Some((400, 0.05)))
      playSound2("water-6", times(5), volume = 3, rate = 1.0, pan = (0.7, -0.5), bandPass = Some((400, 0.05)))

      playSound2("knife-6", times(6), volume = 3, rate = 1.0, pan = (-0.7, 0.5), bandPass = Some((300, 0.05)))
      playSound2("water-6", times(6), volume = 3, rate = 1.0, pan = (0.7, -0.5), bandPass = Some((300, 0.05)))

      playSound2("knife-6", times(7), volume = 3, rate = 1.0, pan = (-0.7, 0.5), bandPass = Some((200, 0.05)))
      playSound2("water-6", times(7), volume = 3, rate = 1.0, pan = (0.7, -0.5), bandPass = Some((200, 0.05)))


      playSound2("knife-6", times(8), volume = 1.0, rate = 1.0, pan = (-0.3, -0.9))
      playSound2("knife-6", times(8), volume = 4.0, rate = 1.0, pan = (-0.4, -0.8), bandPass = Some((100, 0.05)))
      playSound2("water-6", times(8) + 0.505, volume = 1.0, rate = 1.0, pan = (0.3, 0.9))
      playSound2("water-6", times(8) + 0.505, volume = 3.0, rate = 1.0, pan = (0.4, 0.8), bandPass = Some((200, 0.05)))

      playSound2("knife-6", times(9), volume = 1.0, rate = 1.0, pan = (-0.3, -0.9), highPass = Some(3000))
      playSound2("water-6", times(9) + 0.505, volume = 1.0, rate = 1.0, pan = (0.3, 0.9), highPass = Some(3000))

      playSound2("knife-6", times(10), volume = 1.0, rate = 1.0, pan = (-0.3, -0.9), highPass = Some(6000))
      playSound2("water-6", times(10) + 0.505, volume = 1.0, rate = 1.0, pan = (0.3, 0.9), highPass = Some(6000))

      playSound2("knife-6", times(11), volume = 1.0, rate = 1.0, pan = (-0.3, -0.9), highPass = Some(9000))
      playSound2("water-6", times(11) + 0.505, volume = 1.0, rate = 1.0, pan = (0.3, 0.9), highPass = Some(9000))

      playSound2("knife-6", times(12), volume = 1.0, rate = 1.0, pan = (-0.3, -0.9), highPass = Some(10000))
      playSound2("water-6", times(12) + 0.505, volume = 1.0, rate = 1.0, pan = (0.3, 0.9), highPass = Some(10000))


      playSound2("knife-6", times(13), volume = 1.0, rate = 1.0, pan = (-0.7, 0.5))
      playSound2("knife-6", times(13), volume = 4.0, rate = 1.0, pan = (-0.8, 0.6), bandPass = Some((100, 0.05)))
      playSound2("water-6", times(13), volume = 1.0, rate = 1.0, pan = (0.7, -0.5))
      playSound2("water-6", times(13), volume = 3.0, rate = 1.0, pan = (0.8, -0.6), bandPass = Some((200, 0.05)))

      playSound2("knife-6", times(14), volume = 3, rate = 1.0, pan = (-0.7, 0.5), bandPass = Some((400, 0.05)))
      playSound2("water-6", times(14), volume = 3, rate = 1.0, pan = (0.7, -0.5), bandPass = Some((400, 0.05)))

      playSound2("knife-6", times(15), volume = 3, rate = 1.0, pan = (-0.7, 0.5), bandPass = Some((300, 0.05)))
      playSound2("water-6", times(15), volume = 3, rate = 1.0, pan = (0.7, -0.5), bandPass = Some((300, 0.05)))

      playSound2("knife-6", times(16), volume = 3, rate = 1.0, pan = (-0.7, 0.5), bandPass = Some((200, 0.05)))
      playSound2("water-6", times(16), volume = 3, rate = 1.0, pan = (0.7, -0.5), bandPass = Some((200, 0.05)))
    }
  }


  def testLongWater(start: Double = 0): Unit = {
    client.resetClock
    playSound2("water-3", 0, volume = 1.0, rate = 1.0, pan = (-0.5, 0.2), highPass = Some(3000))
    playSound2("water-3", 5, volume = 1.0, rate = 0.99, pan = (0.5, -0.2), highPass = Some(5000))
    playSound2("water-3", 13, volume = 1.0, rate = 1.01, pan = (-0.2, 0.5), highPass = Some(2000))
    playSound2("water-3", 19, volume = 1.0, rate = 1, pan = (0.2, -0.5), highPass = Some(4000))
  }

  def testWaterLow(start: Double = 0): Unit = {
    client.resetClock

    playSound2("water-1", 0, volume = 4, rate = 1.0, pan = (-0.5, 0.2), bandPass = Some((300, 0.05)))
    playSound2("water-1", 4, volume = 4, rate = 0.99, pan = (0.5, -0.2), bandPass = Some((400, 0.05)))
    playSound2("water-1", 8, volume = 4, rate = 1.01, pan = (-0.2, 0.5), bandPass = Some((200, 0.05)))
    playSound2("water-1", 12, volume = 4, rate = 1, pan = (0.2, -0.5), bandPass = Some((300, 0.05)))
  }

  def testLongKnife(start: Double = 0): Unit = {
    client.resetClock
    playSound2("knife-3", 0, volume = 2.0, rate = 1.0, pan = (-0.3, 0.3), lowPass = Some(1000))
    playSound2("knife-3", 0.08, volume = 1.9, rate = 1.01, pan = (0.3, -0.3), lowPass = Some(500))
    playSound2("knife-3", 0.16, volume = 1.8, rate = 0.99, pan = (-0.3, 0.3), lowPass = Some(700))
    playSound2("knife-3", 0.24, volume = 1.7, rate = 1.0, pan = (0.3, -0.3), lowPass = Some(900))

    playSound2("knife-3", 3, volume = 2.0, rate = 1.0, pan = (-0.3, 0.3), highPass = Some(10000))
    playSound2("knife-3", 3.08, volume = 1.9, rate = 1.01, pan = (0.3, -0.3), highPass = Some(9000))
    playSound2("knife-3", 3.16, volume = 1.8, rate = 0.99, pan = (-0.3, 0.3), highPass = Some(8000))
    playSound2("knife-3", 3.24, volume = 1.7, rate = 1.0, pan = (0.3, -0.3), highPass = Some(9000))
  }

  def testEcho(start: Double = 0): Unit = {
    client.resetClock
    playSound2("knife-6", 0, volume = 1.0, rate = 1.0, pan = (-0.3, -0.9))
    playSound2("water-6", 0.4, volume = 1.0, rate = 1.0, pan = (0.3, 0.9))

    playSound2("knife-6", 3, volume = 1.0, rate = 1.5, pan = (-0.3, -0.9), highPass = Some(3000))
    playSound2("water-6", 3.1, volume = 1.0, rate = 1.5, pan = (0.3, 0.9), highPass = Some(3000))

    playSound2("knife-6", 5, volume = 0.9, rate = 1.6, pan = (-0.3, -0.9), highPass = Some(4000))
    playSound2("water-6", 5.1, volume = 0.9, rate = 1.6, pan = (0.3, 0.9), highPass = Some(4000))

    playSound2("knife-6", 7, volume = 0.8, rate = 1.7, pan = (-0.3, -0.9), highPass = Some(5000))
    playSound2("water-6", 7.1, volume = 0.8, rate = 1.7, pan = (0.3, 0.9), highPass = Some(5000))
  }

  def testDouble(start: Double = 0): Unit = {
    client.resetClock
    playSound2("water-3", 0, volume = 1.0, rate = 1.0, pan = (-0.5, 0.2))
    playSound2("water-3", 0, volume = 1.0, rate = 1.005, pan = (0.6, -0.3), highPass = Some(5000))

    playSound2("knife-3", 15, volume = 1.0, rate = 1.0, pan = (0.1, -0.3))
    playSound2("knife-3", 15.005, volume = 1.5, rate = 1.005, pan = (-0.5, -0.8), bandPass = Some((7000, 0.5)))
    playSound2("knife-3", 15.01, volume = 1.5, rate = 0.995, pan = (0.5, 0.8), bandPass= Some((100, 3))/*, lowPass = Some(500)*/)
  }

  def testSub(start: Double = 0): Unit = {
    client.resetClock

    playSound2("water-3", 0, volume = 1.0, rate = 1.0, pan = (-0.3, 0.3))
    playSub("water-3", subFreq = 35, startTime = 0, volume = 1.0, attackTime = 0.1, releaseTime = 0.1, pan = (-0.3, 0.3))
  }

  def playKnife(start: Double = 0): Unit = {
    client.resetClock
    // 890.273
    // 1853.61
    playSound("knife-1", 0 + start, volume = 1.3, rate = 1, pan = -0.9, highPass = Some(890.273))
    playSound("knife-1", 0.01 + start, volume = 1.0, rate = 1.01, pan = 0.9, lowPass = Some(1853.61))

    playSound("knife-1", 2 + start, volume = 1.3, rate = 1, pan = -0.1, lowPass = Some(890.273))
    playSound("knife-1", 2.01 + start, volume = 1.0, rate = 1.01, pan = 0.1, highPass = Some(1853.61))
  }

  def playWater(start: Double = 0): Unit = {
    client.resetClock
    // 750.564, 2712.19
    playSound2("water-1", 0, pan = (-0.1, 0.6), rate = 1.01, highPass = Some(2712.19))
    playSound2("water-1", 0, pan = (0.1, -0.6), rate = 0.99, lowPass = Some(750.564))
  }

  def knifeToWater(start: Double = 0): Unit = {
    client.resetClock
    playSound2("knife-1", start, volume = 1.5, pan = (-0.2, 0.5))
    playSound2("water-1", start + 0.8, volume = 1.0, pan = (0.5, -0.3))
  }

  def init(): Unit = {
    println("Starting up SuperCollider client")
    client.start
    Instrument.setupNodes(client)
    client.send(loadDir(SYNTH_DIR))
    client.send(allocRead(0, s"${KNIFE_SOUNDS_DIR}Knife 1.flac"))
    client.send(allocRead(1, s"${KNIFE_SOUNDS_DIR}Knife 4.flac"))
    client.send(allocRead(2, s"${KNIFE_SOUNDS_DIR}Knife 7.flac"))
    client.send(allocRead(3, s"${KNIFE_SOUNDS_DIR}Knife 10.flac"))
    client.send(allocRead(4, s"${KNIFE_SOUNDS_DIR}Knife 11.flac"))
    client.send(allocRead(5, s"${KNIFE_SOUNDS_DIR}Knife 13.flac"))

    client.send(allocRead(10, s"${WATER_SOUNDS_DIR}Water 1.flac"))
    client.send(allocRead(11, s"${WATER_SOUNDS_DIR}Water 3.flac"))
    client.send(allocRead(12, s"${WATER_SOUNDS_DIR}Water 4.flac"))
    client.send(allocRead(13, s"${WATER_SOUNDS_DIR}Water 5.flac"))
    client.send(allocRead(14, s"${WATER_SOUNDS_DIR}Water 6.flac"))
    client.send(allocRead(15, s"${WATER_SOUNDS_DIR}Water 7.flac"))

  }

  def stop(): Unit = {
    println("Stopping SuperCollider client")
    client.stop
  }
}
