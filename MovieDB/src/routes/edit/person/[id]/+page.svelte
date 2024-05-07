<script>
  import { page } from '$app/stores'
	import { onMount } from 'svelte'
  import currentNavBar from '$lib/currentNavBar';
  import HeaderAdmin from '$lib/layouts/HeaderAdmin.svelte';
  
  $currentNavBar = HeaderAdmin;
  
  const id = $page.url.pathname.split('/')[3]

  let person
  let castMovies = []

  async function load() {
    person = await fetch(
			`https://api.themoviedb.org/3/person/${id}?api_key=799c0bd0b2baaecc6d9301fadfaea7f7&append_to_response=combined_credits&language=en-US`
		).then(res => res.json());
    castMovies = person.combined_credits.cast || []
  }

  onMount(async () => {
    await load(); // 在组件挂载时加载电影数据
    sortMovies(); // 在数据加载后进行排序
  });
  
  function sortMovies() {
  // 在组件挂载后对 castMovies 数组进行排序
  if (castMovies && castMovies.length > 0) {
    castMovies = castMovies.sort((a, b) => {
      const voteA = a.vote_average || 0;
      const voteB = b.vote_average || 0;
      return voteB - voteA;
    });
  }
}

  function getYear(dateString) {
    const releaseDate = new Date(dateString);
    return releaseDate.getFullYear();
  }
</script>


<svelte:head>
	<title>{person ? person.name : 'Person'} - MovieDB</title>
</svelte:head>


{#if person}
<section class="grid grid-cols-1 sm:grid-cols-3 gap-1 lg:grid-cols-4 gap-6 my-6">
  <div class="col-span-1 w-[35%] mx-auto sm:w-auto">
    <img src={`https://image.tmdb.org/t/p/original${person.profile_path}`} alt="Poster" class="rounded-lg mb-8">
    <h3 class="text-2xl text-black/80 mb-4 font-semibold">Personal Info</h3>
    <table class="table-fixed">
      <tbody class="text-left">
        <tr class="border-b-8 border-transparent">
          <th class=" w-32">
            Place of birth
          </th>
          <td class="">
          { person.place_of_birth }
          </td>
        </tr>

        <tr class="border-y-8 border-transparent">
          <th class="">
            Birthday
          </th>
          <td class="">
            {person.birthday}
          </td>
        </tr>
        
        {#if person.deathday}
        <tr class="border-t-8 border-transparent">
          <th class="">
            Birthday
          </th>
          <td class="">
            {person.birthday}
          </td>
        </tr>
        {/if}

      </tbody>
    </table>
  </div>
  

  <div class="lg:col-span-3 sm:col-span-2">
    <h2 class="text-4xl font-extrabold text-black mb-8">
      {person.name}
    </h2>
    <h3 class="text-2xl text-black/80 mb-4 font-semibold">Biography</h3>
    <p class="mb-8">{person.biography}</p>
    
    <h3 class="text-2xl text-black/80 mb-2 font-semibold">Known for</h3>
    <div class="mb-8 flex overflow-x-auto">
      {#each castMovies as movie}
        <a href={`/movie/${movie.id}`} class="flex-shrink-0 w-48 mx-2"> <!-- 设置项的固定宽度，添加外边距 -->
          <div class="flex flex-col items-center mb-4 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-800 hover:shadow-lg group">
            <h5 class="text-center text-grey-300 mb-1">{getYear(movie.release_date)}</h5>
            <img 
              src={`https://image.tmdb.org/t/p/w500${movie.poster_path}`} 
              alt={movie.title}
              class="rounded-lg mb-2 object-cover group-hover:scale-105 transition-all duration-300"
            >
            <h4 class="text-center font-semibold group-hover:text-blue-500/80">{movie.original_title || movie.title}</h4>
          </div>
        </a>
      {/each}
    </div>
  </div>
</section>

  {:else}
  <h2 class="m-auto text-2xl">Loading...</h2>
{/if}
