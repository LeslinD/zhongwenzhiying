<script>
  import { page } from '$app/stores'
	import { onMount } from 'svelte'
  import { API } from '$lib/api'
  import CastMovieCard from '$lib/components/CastMovieCard.svelte';
  import currentNavBar from '$lib/currentNavBar';
  import Header from '$lib/layouts/Header.svelte';
  
  $currentNavBar = Header;
  
  const id = $page.url.pathname.split('/')[2]

  let person
  let castMovies = []

  async function load() {
    const data = await fetch(
			`${API}/personDetail?person_id=${id}`
		).then(res => res.json());
    person = data.person
    castMovies = data.movieList || []
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
  </div>

  <div class="lg:col-span-3 sm:col-span-2">
    <h2 class="text-4xl font-extrabold text-black mb-8">
      {person.name}
    </h2>
    <!--<h3 class="text-2xl text-black/80 mb-4 font-semibold">Biography</h3>
    <p class="mb-8">{person.biography}</p>-->
    
    <h3 class="text-2xl text-black/80 mb-2 font-semibold">Known for</h3>
    <div class="mb-8 flex overflow-x-auto">
      {#each castMovies as movie (show.movie_id)}
        <CastMovieCard item={movie} media=movie />
      {/each}
    </div>
  </div>
</section>

  {:else}
  <h2 class="m-auto text-2xl">Loading...</h2>
{/if}
