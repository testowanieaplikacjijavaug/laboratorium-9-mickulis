package lab9;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class FriendshipsMongoMockitoTest
{

	@InjectMocks
	FriendshipsMongo friendships = new FriendshipsMongo();

	@Mock
	FriendsCollection friends;

	@Test
	public void mockingWorksAsExpected(){
		Person joe = new Person("Joe");
		when(friends.findByName("Joe")).thenReturn(joe);
		assertThat(friends.findByName("Joe")).isEqualTo(joe);
	}

	@Test
	public void alexDoesNotHaveFriends(){
		assertThat(friendships.getFriendsList("Alex")).isEmpty();
	}

	@Test
	public void joeHas5Friends(){
		List<String> expected = Arrays.asList("Karol","Dawid","Maciej","Tomek","Adam");
		Person joe = Mockito.mock(Person.class);
		when(friends.findByName("Joe")).thenReturn(joe);
		when(joe.getFriends()).thenReturn(expected);
		assertThat(friendships.getFriendsList("Joe")).hasSize(5).containsOnly("Karol","Dawid","Maciej","Tomek","Adam");
	}


	@Test
	public void addFriend_Existing()
	{
		Person joe = Mockito.mock(Person.class);
		when(friends.findByName("Joe")).thenReturn(joe);

		friendships.addFriend("Joe", "Terry");

		assertAll(
			() -> verify(joe, times(1)).addFriend("Terry"),
			() -> verify(friends, times(1)).save(joe)
		);
	}

	@Test
	public void makeFriends()
	{
		Person joe = Mockito.mock(Person.class);
		when(friends.findByName("Joe")).thenReturn(joe);
		Person terry = Mockito.mock(Person.class);
		when(friends.findByName("Terry")).thenReturn(terry);
		friendships.makeFriends("Joe", "Terry");
		assertAll(
			() -> verify(joe, times(1)).addFriend("Terry"),
			() -> verify(terry, times(1)).addFriend("Joe"),
			() -> verify(friends, times(1)).save(joe),
			() -> verify(friends, times(1)).save(terry)
		);
	}

	@Test
	public void areFriends_EmptyFriendlist()
	{
		Person joe = Mockito.mock(Person.class);
		when(friends.findByName("Joe")).thenReturn(joe);
		when(joe.getFriends()).thenReturn(new ArrayList<>());

		assertFalse(friendships.areFriends("Joe", "Misery"));
	}

	@Test
	public void areFriends_NotOnTheList()
	{
		Person joe = Mockito.mock(Person.class);
		when(friends.findByName("Joe")).thenReturn(joe);
		when(joe.getFriends()).thenReturn(Arrays.asList("Me", "Myself", "I"));

		assertFalse(friendships.areFriends("Joe", "Misery"));
	}

	@Test
	public void areFriends_OnTheList()
	{
		Person joe = Mockito.mock(Person.class);
		when(friends.findByName("Joe")).thenReturn(joe);
		when(joe.getFriends()).thenReturn(Arrays.asList("Friend", "Another", "And Another"));

		assertTrue(friendships.areFriends("Joe", "Friend"));
	}

	@Test
	public void areFriends_MultiplesOnTheList()
	{
		Person joe = Mockito.mock(Person.class);
		when(friends.findByName("Joe")).thenReturn(joe);
		when(joe.getFriends()).thenReturn(Arrays.asList("Friend", "Another", "Friend", "And Another", "Friend"));

		assertTrue(friendships.areFriends("Joe", "Friend"));
	}

	@Test
	public void areFriends_NullFriend()
	{
		Person joe = Mockito.mock(Person.class);
		when(friends.findByName("Joe")).thenReturn(joe);
		when(joe.getFriends()).thenReturn(Arrays.asList("Friend", "Another", "And Another"));

		assertFalse(friendships.areFriends("Joe", null));
	}

	@Test
	public void areFriends_NullPerson()
	{
		when(friends.findByName(null)).thenReturn(null);
		friendships.areFriends(null, "Friend");

		verify(friends, times(1)).findByName(null);
	}
}
