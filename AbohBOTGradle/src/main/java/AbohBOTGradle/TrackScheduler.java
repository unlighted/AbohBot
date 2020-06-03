package AbohBOTGradle;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;


/**
 * This class schedules tracks for the audio player. It contains the queue of tracks and various methods to manipulate the track queue.
 */

public class TrackScheduler extends AudioEventAdapter {
 
  private final AudioPlayer player;
  private final BlockingDeque<AudioTrack> queue;
 
  /**
   * @param player The audio player this scheduler uses
   */

public TrackScheduler(AudioPlayer player) {
   
	this.player = player;
    this.queue = new LinkedBlockingDeque<>();
  
    

}
  

/**
 * Add the next track to queue or play right away if nothing is in the queue.
 *
 * @param audioTrack The track to play or add to queue.
 */

  public void addToQueue(AudioTrack audioTrack) {
    queue.addLast(audioTrack);
    startNextTrack(true);
  }

  
  
  public List<AudioTrack> drainQueue() {
    List<AudioTrack> drainedQueue = new ArrayList<>();
    queue.drainTo(drainedQueue);
    return drainedQueue;
  }

  public void playNow(AudioTrack audioTrack, boolean clearQueue) {
    if (clearQueue) {
      queue.clear();
    }

    queue.addFirst(audioTrack);
    startNextTrack(false);
  }

  
  
  public void skip() {
    startNextTrack(false);
  }

  
  
  
  private void startNextTrack(boolean noInterrupt) {
    AudioTrack next = queue.pollFirst();

    if (next != null) {
      if (!player.startTrack(next, noInterrupt)) {
        queue.addFirst(next);
      }
    } else {
      player.stopTrack();
     
      
    }
  }


  @Override
  public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
    if (endReason.mayStartNext) {
    	startNextTrack(true);
     
    
   }
 
    
    
      }
    
  

  @Override
  public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
   

    startNextTrack(false);
  }


  
  
}