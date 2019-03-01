package org.jpf.ci.gitlabs;

public class ProjecInfo
{

	private String id;
	private String description;
	/**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  private String name;
	private String default_branch;
	private String PUBLIC = " ";
	// private String tag_list="null";
	private String archived;
	private String visibility_level;
	private String ssh_url_to_repo;
	private String http_url_to_repo;
	private String web_url;
	private String name_with_namespace;
	private String path_with_namespace;
	private String created_at;
	private String last_activity_at;
	private String creator_id;
	private Owners owner[];

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getDefault_branch()
	{
		return default_branch;
	}

	public void setDefault_branch(String default_branch)
	{
		this.default_branch = default_branch;
	}

	public String getPublic()
	{
		return PUBLIC;
	}

	public void setPublic(String PUBLIC)
	{
		this.PUBLIC = PUBLIC;
	}

	public String getArchived()
	{
		return archived;
	}

	public void setArchived(String archived)
	{
		this.archived = archived;
	}

	public String getVisibility_level()
	{
		return visibility_level;
	}

	public void setVisibility_level(String visibility_level)
	{
		this.visibility_level = visibility_level;
	}

	public String getSsh_url_to_repo()
	{
		return ssh_url_to_repo;
	}

	public void setSsh_url_to_repo(String ssh_url_to_repo)
	{
		this.ssh_url_to_repo = ssh_url_to_repo;
	}

	public String getHttp_url_to_repo()
	{
		return http_url_to_repo;
	}

	public void setHttp_url_to_repo(String http_url_to_repo)
	{
		this.http_url_to_repo = http_url_to_repo;
	}

	public String getWeb_url()
	{
		return web_url;
	}

	public void setWeb_url(String web_url)
	{
		this.web_url = web_url;
	}

	public String getName_with_namespace()
	{
		return name_with_namespace;
	}

	public void setName_with_namespace(String name_with_namespace)
	{
		this.name_with_namespace = name_with_namespace;
	}

	public String getPath_with_namespace()
	{
		return path_with_namespace;
	}

	public void setPath_with_namespace(String path_with_namespace)
	{
		this.path_with_namespace = path_with_namespace;
	}

	public String getCreated_at()
	{
		return created_at;
	}

	public void setCreated_at(String created_at)
	{
		this.created_at = created_at;
	}

	public String getLast_activity_at()
	{
		return last_activity_at;
	}

	public void setLast_activity_at(String last_activity_at)
	{
		this.last_activity_at = last_activity_at;
	}

	public String getCreator_id()
	{
		return creator_id;
	}

	public void setCreator_id(String creator_id)
	{
		this.creator_id = creator_id;
	}

	public Owners[] getOwner()
	{
		return owner;
	}

	public void setOwner(Owners[] owner)
	{
		this.owner = owner;
	}

	@Override
	public String toString()
	{
		return "ProjecInfo [ id=" + id + ", name="+name+",description=" + description
				+ ", default_branch=" + default_branch + ", PUBLIC=" + PUBLIC
				+ ", archived=" + archived + ", visibility_level="
				+ visibility_level + ", ssh_url_to_repo=" + ssh_url_to_repo
				+ ", http_url_to_repo=" + http_url_to_repo + ", web_url="
				+ web_url + ", name_with_namespace=" + name_with_namespace
				+ ", path_with_namespace=" + path_with_namespace
				+ ", created_at=" + created_at + ", last_activity_at="
				+ last_activity_at + ", creator_id=" + creator_id + ", owner="
				+ "]";
	}

}
