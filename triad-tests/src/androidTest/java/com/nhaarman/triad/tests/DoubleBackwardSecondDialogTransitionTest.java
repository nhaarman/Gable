package com.nhaarman.triad.tests;

import com.nhaarman.triad.TestActivityInstrumentationTestCase;
import com.nhaarman.triad.tests.firstdialog.FirstDialogScreen;
import com.nhaarman.triad.tests.seconddialog.SecondDialogScreen;

import static com.nhaarman.triad.tests.utils.ViewWaiter.viewHasAlpha;
import static com.nhaarman.triad.tests.utils.ViewWaiter.viewNotPresent;
import static com.nhaarman.triad.tests.utils.ViewWaiter.viewVisible;
import static com.nhaarman.triad.tests.utils.ViewWaiter.waitUntil;
import static org.assertj.android.api.Assertions.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;

public class DoubleBackwardSecondDialogTransitionTest extends TestActivityInstrumentationTestCase {

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    getInstrumentation().runOnMainSync(new Runnable() {
      @Override
      public void run() {
        mFlow.goTo(new FirstDialogScreen());
        mFlow.goTo(new SecondDialogScreen());
      }
    });

    waitUntil(viewVisible(mDialogHolder, R.id.view_dialog_second));

    getInstrumentation().runOnMainSync(new Runnable() {
      @Override
      public void run() {
        mFlow.goBack();
        mFlow.goBack();
      }
    });

    waitUntil(viewNotPresent(mDialogHolder, R.id.view_dialog_first));
    waitUntil(viewHasAlpha(mDimmerView, 0f));
  }

  public void test_afterTransition_secondDialogIsNotPresent() {
    assertThat(mDialogHolder.findViewById(R.id.view_dialog_second), is(nullValue()));
  }

  public void test_afterTransition_firstDialogIsNotPresent() {
    assertThat(mDialogHolder.findViewById(R.id.view_dialog_first), is(nullValue()));
  }

  public void test_afterTransition_originalScreenIsPresent() {
    assertThat(mScreenHolder.findViewById(R.id.view_screen_first), is(not(nullValue())));
  }

  public void test_afterTransition_dimmerView_isVisible() {
    assertThat(mDimmerView).isVisible();
  }

  public void test_afterTransition_dimmerView_isFullyTranslucent() {
    assertThat(mDimmerView).hasAlpha(0f);
  }

  public void test_afterTransition_dimmerView_isNotClickable() {
    assertThat(mDimmerView).isNotClickable();
  }

  public void test_afterTransition_dialogHolder_isVisible() {
    assertThat(mDialogHolder).isVisible();
  }
}
