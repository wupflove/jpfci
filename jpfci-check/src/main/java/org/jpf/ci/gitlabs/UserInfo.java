package org.jpf.ci.gitlabs;

import java.util.Arrays;

public class UserInfo
{

	private String name;
	private String username;
	private String id;
	private String state;
	private String avatar_url;
	private String created_at;
	private boolean is_admin;
	private String bio;
	private String skype;
	private String linkedin;
	private String twitter;
	private String website_url;
	private String email;
	private int theme_id;
	private int color_scheme_id;
	private int projects_limit;
	private String current_sign_in_at;
	private Identities identities[];
	private boolean can_create_group;
	private boolean can_create_project;
	private boolean two_factor_enabled;

	public UserInfo()
	{
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getState()
	{
		return state;
	}

	public void setState(String state)
	{
		this.state = state;
	}

	public String getAvatar_url()
	{
		return avatar_url;
	}

	public void setAvatar_url(String avatar_url)
	{
		this.avatar_url = avatar_url;
	}

	public String getCreated_at()
	{
		return created_at;
	}

	public void setCreated_at(String created_at)
	{
		this.created_at = created_at;
	}

	public boolean isIs_admin()
	{
		return is_admin;
	}

	public void setIs_admin(boolean is_admin)
	{
		this.is_admin = is_admin;
	}

	public String getBio()
	{
		return bio;
	}

	public void setBio(String bio)
	{
		this.bio = bio;
	}

	public String getSkype()
	{
		return skype;
	}

	public void setSkype(String skype)
	{
		this.skype = skype;
	}

	public String getLinkedin()
	{
		return linkedin;
	}

	public void setLinkedin(String linkedin)
	{
		this.linkedin = linkedin;
	}

	public String getTwitter()
	{
		return twitter;
	}

	public void setTwitter(String twitter)
	{
		this.twitter = twitter;
	}

	public String getWebsite_url()
	{
		return website_url;
	}

	public void setWebsite_url(String website_url)
	{
		this.website_url = website_url;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public int getTheme_id()
	{
		return theme_id;
	}

	public void setTheme_id(int theme_id)
	{
		this.theme_id = theme_id;
	}

	public int getColor_scheme_id()
	{
		return color_scheme_id;
	}

	public void setColor_scheme_id(int color_scheme_id)
	{
		this.color_scheme_id = color_scheme_id;
	}

	public int getProjects_limit()
	{
		return projects_limit;
	}

	public void setProjects_limit(int projects_limit)
	{
		this.projects_limit = projects_limit;
	}

	public String getCurrent_sign_in_at()
	{
		return current_sign_in_at;
	}

	public void setCurrent_sign_in_at(String current_sign_in_at)
	{
		this.current_sign_in_at = current_sign_in_at;
	}

	public Identities[] getIdentities()
	{
		return identities;
	}

	public void setIdentities(Identities[] identities)
	{
		this.identities = identities;
	}

	public boolean isCan_create_group()
	{
		return can_create_group;
	}

	public void setCan_create_group(boolean can_create_group)
	{
		this.can_create_group = can_create_group;
	}

	public boolean isCan_create_project()
	{
		return can_create_project;
	}

	public void setCan_create_project(boolean can_create_project)
	{
		this.can_create_project = can_create_project;
	}

	public boolean isTwo_factor_enabled()
	{
		return two_factor_enabled;
	}

	public void setTwo_factor_enabled(boolean two_factor_enabled)
	{
		this.two_factor_enabled = two_factor_enabled;
	}

	@Override
	public String toString()
	{
		return "UserInfo [name=" + name + ", username=" + username + ", id=" + id + ", state=" + state + ", avatar_url="
				+ avatar_url + ", created_at=" + created_at + ", is_admin=" + is_admin + ", bio=" + bio + ", skype="
				+ skype + ", linkedin=" + linkedin + ", twitter=" + twitter + ", website_url=" + website_url
				+ ", email=" + email + ", theme_id=" + theme_id + ", color_scheme_id=" + color_scheme_id
				+ ", projects_limit=" + projects_limit + ", current_sign_in_at=" + current_sign_in_at + ", identities="
				+ Arrays.toString(identities) + ", can_create_group=" + can_create_group + ", can_create_project="
				+ can_create_project + ", two_factor_enabled=" + two_factor_enabled + "]";
	}

}
