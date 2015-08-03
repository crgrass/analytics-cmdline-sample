/*
 * Copyright (c) 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package test.Java;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

/**
 * @author cgrass@google.com (Your Name Here)
 *
 */
public class TestRunner {
  public static void main(String[] args) {
    Result result = JUnitCore.runClasses(CSVReadersTest.class);
    System.out.println("Failure Count: " + result.getFailureCount());
    for (Failure failure : result.getFailures()) {
      System.out.println("Failures: " + failure.toString());
      System.out.println("Failure Message: " + failure.getMessage());
      System.out.println("Failure Trace: " + failure.getTrace());
      System.out.println("Failure Description: " + failure.getDescription());
      System.out.println("");
    }
    System.out.println("\nSuccessful?");
    System.out.println(result.wasSuccessful());
  }
}
