package com.ansill.lock.multilock.test;

import com.ansill.lock.autolock.AutoLock;
import com.ansill.lock.autolock.LockedAutoLock;
import com.ansill.lock.multilock.MultiLock;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.junit.jupiter.api.Assertions.*;

class MultiLockTest{

    @Test
    void testNullLock(){

        // Create MultiLock
        assertThrows(IllegalArgumentException.class, () -> new MultiLock((Lock) null));
    }

    @Test
    void testNullLocks(){

        // Create MultiLock
        assertThrows(IllegalArgumentException.class, () -> new MultiLock(new ReentrantLock(), null));
    }

    @Test
    void testDuplicateLock(){

        // Single lock
        ReentrantLock rl = new ReentrantLock();

        // Create MultiLock
        assertThrows(IllegalArgumentException.class, () -> new MultiLock(rl, rl));
    }

    @Test
    void testInnerDuplicateLock(){

        // Single lock
        ReentrantLock rl = new ReentrantLock();

        MultiLock al = new MultiLock(new ReentrantLock(), rl);

        // Create MultiLock
        assertThrows(IllegalArgumentException.class, () -> new MultiLock(rl, new ReentrantLock(), al));
    }

    @Test
    void testMultiLock(){

        // Create lock
        ReentrantLock rl = new ReentrantLock();

        // Create MultiLock
        MultiLock al = new MultiLock(rl);

        // Lock it
        al.lock();

        // Assert that lock is locked
        assertTrue(rl.isLocked());

        // Unlock it
        al.unlock();

        // Assert that lock is unlocked
        assertFalse(rl.isLocked());
    }

    @Test
    void testAutoLockDoLock(){

        // Create lock
        ReentrantLock rl = new ReentrantLock();

        // Create AutoLock
        AutoLock al = new MultiLock(rl);

        // Lock it
        try(LockedAutoLock ignored = al.doLock()){

            // Assert that lock is locked
            assertTrue(rl.isLocked());

        }

        // Assert that lock is unlocked
        assertFalse(rl.isLocked());
    }

    @Test
    void testAutoLockDoLockInterruptibly() throws Exception{

        // Create lock
        ReentrantLock rl = new ReentrantLock();

        // Create AutoLock
        AutoLock al = new MultiLock(rl);

        // Lock it
        try(LockedAutoLock ignored = al.doLockInterruptibly()){

            // Assert that lock is locked
            assertTrue(rl.isLocked());

        }

        // Assert that lock is unlocked
        assertFalse(rl.isLocked());
    }

    @Test
    void testAutoLockDoTryLock() throws Exception{

        // Create lock
        ReentrantLock rl = new ReentrantLock();

        // Create AutoLock
        AutoLock al = new MultiLock(rl);

        // Lock it
        try(LockedAutoLock ignored = al.doTryLock()){

            // Assert that lock is locked
            assertTrue(rl.isLocked());

        }

        // Assert that lock is unlocked
        assertFalse(rl.isLocked());
    }

    @Test
    void testAutoLockDoTryLockDuration() throws Exception{

        // Create lock
        ReentrantLock rl = new ReentrantLock();

        // Create MultiLock
        MultiLock al = new MultiLock(rl);

        // Lock it
        try(LockedAutoLock ignored = al.doTryLock(1, TimeUnit.MILLISECONDS)){

            // Assert that lock is locked
            assertTrue(rl.isLocked());

        }

        // Assert that lock is unlocked
        assertFalse(rl.isLocked());
    }

    @Test
    void testAutoLockTryLockTimeout() throws Exception{

        // Create lock
        ReentrantLock rl = new ReentrantLock();

        // Create AutoLock
        AutoLock al = new MultiLock(rl);

        // Lock it
        try(LockedAutoLock ignored = al.doLock()){

            // Assert that lock is locked
            assertTrue(rl.isLocked());

            // CDL
            CountDownLatch cdl = new CountDownLatch(1);

            // AtomicBoolean
            AtomicBoolean result = new AtomicBoolean();

            // Fire new thread (tryLock will accept currentThread as 'true', meaning new Thread is needed to get this condition to fail)
            new Thread(() -> {
                result.set(rl.tryLock());
                cdl.countDown();
            }).start();

            // Wait
            cdl.await();

            // Assert
            assertFalse(result.get());
        }

        // Assert that lock is unlocked
        assertFalse(al.isLocked());

        // Assert that lock is unlocked
        assertFalse(rl.isLocked());
    }

    @Test
    void testAutoLockTryLockDurationTimeout() throws Exception{

        // Create lock
        ReentrantLock rl = new ReentrantLock();

        // Create AutoLock
        AutoLock al = new MultiLock(rl);

        // Lock it
        try(LockedAutoLock ignored = al.doLock()){

            // Assert that lock is locked
            assertTrue(rl.isLocked());

            // CDL
            CountDownLatch cdl = new CountDownLatch(1);

            // AtomicBoolean
            AtomicBoolean result = new AtomicBoolean();

            // Fire new thread (tryLock will accept currentThread as 'true', meaning new Thread is needed to get this condition to fail)
            new Thread(() -> {
                try{
                    result.set(rl.tryLock(1, TimeUnit.MILLISECONDS));
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
                cdl.countDown();
            }).start();

            // Wait
            cdl.await();

            // Assert
            assertFalse(result.get());
        }

        // Assert that lock is unlocked
        assertFalse(al.isLocked());

        // Assert that lock is unlocked
        assertFalse(rl.isLocked());
    }

    @Test
    void testAutoLockDoTryLockTimeout() throws Exception{

        // Create lock
        ReentrantLock rl = new ReentrantLock();

        // Create AutoLock
        AutoLock al = new MultiLock(rl);

        // Lock it
        try(LockedAutoLock ignored = al.doLock()){

            // Assert that lock is locked
            assertTrue(rl.isLocked());

            // CDL
            CountDownLatch cdl = new CountDownLatch(1);

            // AtomicBoolean
            AtomicReference<TimeoutException> result = new AtomicReference<>();

            // Fire new thread (tryLock will accept currentThread as 'true', meaning new Thread is needed to get this condition to fail)
            new Thread(() -> {
                try{
                    al.doTryLock();
                }catch(TimeoutException e){
                    result.set(e);
                }
                cdl.countDown();
            }).start();

            // Wait
            cdl.await();

            // Assert
            assertThrows(TimeoutException.class, () -> {
                TimeoutException te = result.get();
                if(te == null) return;
                throw te;
            });
        }

        // Assert that lock is unlocked
        assertFalse(rl.isLocked());
    }

    @Test
    void testAutoLockDoTryLockDurationTimeout() throws Exception{

        // Create lock
        ReentrantLock rl = new ReentrantLock();

        // Create AutoLock
        AutoLock al = new MultiLock(rl);

        // Lock it
        try(LockedAutoLock ignored = al.doLock()){

            // Assert that lock is locked
            assertTrue(rl.isLocked());

            // CDL
            CountDownLatch cdl = new CountDownLatch(1);

            // AtomicBoolean
            AtomicReference<TimeoutException> result = new AtomicReference<>();

            // Fire new thread (tryLock will accept currentThread as 'true', meaning new Thread is needed to get this condition to fail)
            new Thread(() -> {
                try{
                    al.doTryLock(1, TimeUnit.MILLISECONDS);
                }catch(TimeoutException e){
                    result.set(e);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
                cdl.countDown();
            }).start();

            // Wait
            cdl.await();

            // Assert
            assertThrows(TimeoutException.class, () -> {
                TimeoutException te = result.get();
                if(te == null) return;
                throw te;
            });
        }

        // Assert that lock is unlocked
        assertFalse(rl.isLocked());
    }
}